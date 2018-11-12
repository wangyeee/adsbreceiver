package adsbrecorder.receiver.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import adsbrecorder.jni.Aircraft;
import adsbrecorder.receiver.Receiver;
import adsbrecorder.receiver.service.UploadService;

@Service
public class UploadServiceImpl implements UploadService {

    private static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    private Map<Integer, Aircraft> latestAircrafts;
    private List<Aircraft> allAircrafts;

    @Value("${adsbrecorder.client.name}")
    private String clientName;

    @Value("${adsbrecorder.client.key}")
    private String clientKey;

    @Value("${adsbrecorder.client.retry:5}")
    private int retry;

    @Value("${adsbrecorder.client.login}")
    private String loginUri;

    private volatile boolean connected;

    private RestTemplate restTemplate;

    private String authToken;

    @Autowired
    public UploadServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = requireNonNull(restTemplateBuilder).build();
        initCache();
        connected = false;
    }

    private void connect() {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(String.format("%s?name=%s&key=%s", loginUri, clientName, clientKey), Map.class);
        if (response.containsKey("token")) {
            authToken = String.valueOf(response.get("token"));
            connected = true;
            System.out.println("Connected to server, token: \n" + authToken);
        }
    }

    @Override
    public void uploadAircraftData(Aircraft aircraft) {
        if (aircraft != null) {
            Aircraft previous = latestAircrafts.get(aircraft.getAddressICAO());
            if (previous != null && aircraft.getLastTimeSeen() - previous.getLastTimeSeen() < 2)
                return;
            latestAircrafts.put(aircraft.getAddressICAO(), aircraft);
            allAircrafts.add(aircraft);
        }
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 2000) // 5s
    public void keepConnection() {
        int i = 0;
        do {
            if (connected)
                return;
            connect();
            i++;
        } while (i < retry);
        if (!connected) {
            logger.info("Failed to connect after {} retries, exit", retry);
            Receiver.exit(1);
        }
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000) // 10s
    public void doUpload() {
        if (connected) {
            List<Aircraft> aircrafts = this.allAircrafts;
            initCache();
            if (aircrafts.size() > 0) {
                logger.info("Uploading {} data records.", aircrafts.size());
                aircrafts.forEach((a) -> logger.info(a.toString()));
            } else {
                logger.info("No data to upload.");
            }
        }
    }

    private void initCache() {
        latestAircrafts = new ConcurrentHashMap<Integer, Aircraft>();
        allAircrafts = new CopyOnWriteArrayList<Aircraft>();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}

package adsbrecorder.receiver.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import adsbrecorder.jni.Aircraft;
import adsbrecorder.receiver.Receiver;
import adsbrecorder.receiver.service.UploadService;

@Service
public class UploadServiceImpl implements UploadService, ResponseErrorHandler {

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

    @Value("${adsbrecorder.client.upload}")
    private String uploadUri;

    private volatile boolean connected;
    
    private volatile boolean serverError;

    private String authToken;

    public UploadServiceImpl() {
        initCache();
        connected = false;
        serverError = false;
    }

    private void connect() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(this);
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(String.format("%s?name=%s&key=%s", loginUri, clientName, clientKey), Map.class);
        if (response.containsKey("token")) {
            authToken = String.valueOf(response.get("token"));
            connected = true;
            System.out.println("Connected to server, token: \n" + authToken);
        }
    }

    private void postAircraftData(List<Aircraft> aircrafts) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(this);
        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().add("Authorization", new StringBuilder("Bearer ").append(authToken).toString());
            return execution.execute(request, body);
        }));
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(uploadUri, aircrafts, Map.class);
        if (response.hasBody()) {
            System.err.println("new_records: " + response.getBody().get("new_records"));
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
            try {
                connect();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            i++;
        } while (i < retry);
        if (!serverError) {
            if (!connected) {
                logger.info("Failed to connect after {} retries, exit", retry);
                Receiver.exit(1);
            }
        }
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000) // 10s
    public void doUpload() {
        if (connected) {
            List<Aircraft> aircrafts = this.allAircrafts;
            initCache();
            if (aircrafts.size() > 0) {
                logger.info("Uploading {} data records.", aircrafts.size());
                postAircraftData(aircrafts);
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

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR 
            || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        System.err.println(response.getStatusCode());
        System.err.println(response.getStatusText());
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            connected = false;
            serverError = true;
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                connected = false;
            }
        }
    }
}

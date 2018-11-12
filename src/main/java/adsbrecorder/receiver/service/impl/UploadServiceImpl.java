package adsbrecorder.receiver.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import adsbrecorder.jni.Aircraft;
import adsbrecorder.receiver.service.UploadService;

@Service
public class UploadServiceImpl implements UploadService {

    private static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    private Map<Integer, Aircraft> latestAircrafts;
    private List<Aircraft> allAircrafts;

    public UploadServiceImpl() {
        initCache();
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

    @Scheduled(fixedDelay = 10000) // 10s
    public void doUpload() {
        List<Aircraft> aircrafts = this.allAircrafts;
        initCache();
        if (aircrafts.size() > 0) {
            logger.info("Uploading {} data records.", aircrafts.size());
            aircrafts.forEach((a) -> logger.info(a.toString()));
        } else {
            logger.info("No data to upload.");
        }
    }

    private void initCache() {
        latestAircrafts = new ConcurrentHashMap<Integer, Aircraft>();
        allAircrafts = new CopyOnWriteArrayList<Aircraft>();
    }
}

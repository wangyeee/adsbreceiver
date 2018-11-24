package adsbrecorder.receiver;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import adsbrecorder.jni.Aircraft;
import adsbrecorder.jni.Dump1090Native;
import adsbrecorder.jni.NewAircraftCallback;
import adsbrecorder.receiver.repo.FlightRecordStorage;
import adsbrecorder.receiver.service.UploadService;

@Component
public class AirplaneLocalMonitor extends Thread implements NewAircraftCallback {
    private static Logger logger = LoggerFactory.getLogger(AirplaneLocalMonitor.class);

    @Value("${adsbrecorder.rtl_bias_tee:false}")
    private boolean biasTee;
    @Value("${adsbrecorder.rtl_device:0}")
    private int deviceIndex;

    private Dump1090Native dump1090;

    private UploadService uploadService;

    private FlightRecordStorage flightRecordStorage;

    @Override
    public void aircraftFound(Aircraft aircraft) {
        flightRecordStorage.saveRecord(aircraft);
        uploadService.uploadAircraftData(aircraft);
    }

    @Override
    public void run() {
        if (dump1090 != null) {
            try {
                logger.info("Set bias T: {}", biasTee);
                dump1090.setBiasTee(biasTee);
                logger.info("Starting monitor.");
                dump1090.startMonitor(this);
            } catch (IOException e) {
            }
        }
    }

    @Autowired
    public AirplaneLocalMonitor(UploadService uploadService, FlightRecordStorage flightRecordStorage) {
        setName(getClass().getName());
        this.uploadService = requireNonNull(uploadService);
        this.flightRecordStorage = requireNonNull(flightRecordStorage);
    }

    @PostConstruct
    public void initLocalReceiver() {
        dump1090 = Dump1090Native.getInstance(deviceIndex);
    }
}

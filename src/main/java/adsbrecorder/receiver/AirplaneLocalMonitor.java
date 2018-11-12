package adsbrecorder.receiver;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import adsbrecorder.jni.Aircraft;
import adsbrecorder.jni.Dump1090Native;
import adsbrecorder.jni.NewAircraftCallback;
import adsbrecorder.receiver.service.UploadService;

@Component
public class AirplaneLocalMonitor extends Thread implements NewAircraftCallback {

    @Value("${adsbrecorder.rtl_bias_tee:false}")
    private boolean biasTee;
    @Value("${adsbrecorder.rtl_device:0}")
    private int deviceIndex;

    private Dump1090Native dump1090;

    private UploadService uploadService;

    @Override
    public void aircraftFound(Aircraft aircraft) {
        uploadService.uploadAircraftData(aircraft);
    }

    @Override
    public void run() {
        try {
            if (dump1090 != null) {
                dump1090.setBiasTee(biasTee);
                dump1090.startMonitor(this);
            }
        } catch (IOException e) {
        }
    }

    public AirplaneLocalMonitor(UploadService uploadService) {
        setName(getClass().getName());
        this.uploadService = requireNonNull(uploadService);
    }

    @PostConstruct
    public void initLocalReceiver() {
        dump1090 = requireNonNull(Dump1090Native.getInstance(deviceIndex));
    }
}

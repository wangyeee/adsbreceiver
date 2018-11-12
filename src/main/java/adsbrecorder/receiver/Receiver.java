package adsbrecorder.receiver;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import adsbrecorder.jni.Dump1090Native;

@SpringBootApplication
@ComponentScan(basePackages = {"adsbrecorder.receiver"})
public class Receiver implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    public static void main(String[] args) {
        SpringApplication.run(Receiver.class, args);
    }

    private AirplaneLocalMonitor airplaneMonitor;

    @Autowired
    public Receiver(AirplaneLocalMonitor airplaneMonitor) {
        this.airplaneMonitor = requireNonNull(airplaneMonitor);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("CommandLineRunner::main");
        List<Integer> allReceivers = Dump1090Native.listAllReceivers();
        if (allReceivers.isEmpty()) {
            logger.error("No RTL-SDR receiver found, exit");
            return;
        }
        logger.info("Found {} RTL-SDR receiver(s)", allReceivers.size());
        airplaneMonitor.start();
    }
}

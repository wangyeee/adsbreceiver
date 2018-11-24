package adsbrecorder.receiver;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import adsbrecorder.jni.Dump1090Native;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"adsbrecorder.receiver", "adsbrecorder.receiver.service.impl", "adsbrecorder.receiver.repo"})
public class Receiver implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication receiver = new SpringApplication(Receiver.class);
        receiver.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = receiver.run(args);
        //applicationContext = SpringApplication.run(Receiver.class, args);
    }

    public static void exit(int code) {
        if (applicationContext == null) {
            System.exit(code);
        } else {
            System.exit(SpringApplication.exit(applicationContext, () -> code));
        }
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
            exit(2);
        }
        logger.info("Found {} RTL-SDR receiver(s)", allReceivers.size());
        airplaneMonitor.start();
    }
}

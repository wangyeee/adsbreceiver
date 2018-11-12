# adsbreceiver
The Java client for adsbrecorder server.

## Build and Run
1. Clone and build the [JNI library](https://github.com/wangyeee/dump1090/tree/jni) which passes data from RTL SDR device to Java objects. And add the path of the .so file to java.library.path.
2. Setup the [adsbrecorder](https://github.com/wangyeee/adsbrecorder) server.
3. TBD: Create src/main/resources/application.properties with connection credentials for the server.
4. Run `mvn package` to build jar for this application.
5. Simply run `java -jar adsb-receiver-<version>.jar` to start this application. It will start upload data to the remote server.

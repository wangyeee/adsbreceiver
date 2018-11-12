package adsbrecorder.jni;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dump1090Native {

    private static Map<Integer, Dump1090Native> instances;

    private static boolean nativeLibraryLoaded;

    static {
        try {
            System.loadLibrary("dump1090");
            nativeLibraryLoaded = true;
        } catch (UnsatisfiedLinkError t) {
            nativeLibraryLoaded = false;
        }
        if (nativeLibraryLoaded) {
            try {
                List<Integer> devs = listAllReceivers();
                instances = new HashMap<>();
                for (Integer index : devs) {
                    instances.put(index, new Dump1090Native(index));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int inUse;
    private int devIndex;

    private Dump1090Native(int devIndex) {
        this.devIndex = devIndex;
        this.inUse = 0;
    }

    public static Dump1090Native defaultInstance() {
        return getInstance(0);
    }

    public static Dump1090Native getInstance(int index) {
        if (!nativeLibraryLoaded)
            return null;
        Dump1090Native obj = instances.get(index);
        if (obj != null) {
            if (obj.inUse != 0)
                return null;
            return obj;
        }
        return null;
    }

    public void startMonitor(NewAircraftCallback callback) throws IOException {
        startMonitor(devIndex, callback);
    }

    public int getDeviceIndex() {
        return devIndex;
    }

    public native void startMonitor(int devIndex, NewAircraftCallback callback) throws IOException;
    public native void stopMonitor() throws IOException;
    public native void setBiasTee(boolean biasTee) throws IOException;
    public static native List<Integer> listAllReceivers() throws IOException;
}

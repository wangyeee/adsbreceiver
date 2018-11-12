package adsbrecorder.receiver.service;

import adsbrecorder.jni.Aircraft;

public interface UploadService {

    boolean isConnected();
    void uploadAircraftData(Aircraft aircraft);
}

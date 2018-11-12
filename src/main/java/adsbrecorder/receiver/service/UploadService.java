package adsbrecorder.receiver.service;

import adsbrecorder.jni.Aircraft;

public interface UploadService {

    void uploadAircraftData(Aircraft aircraft);
}

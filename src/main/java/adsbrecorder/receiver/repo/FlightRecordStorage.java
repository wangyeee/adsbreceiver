package adsbrecorder.receiver.repo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import adsbrecorder.jni.Aircraft;

@Component
public class FlightRecordStorage {

    private final static String dbFileName = "rec.db";
    private final static String collectionName = "Aircrafts";

    @Value("${adsbrecorder.stdalone.db_loc}")
    private String dbFilesDir;

    private boolean dbInitialized;

    private Nitrite db;
    private NitriteCollection collection;

    @PostConstruct
    public void initDB() {
        dbInitialized = false;
        if (dbFilesDir.startsWith("~")) {
            dbFilesDir = String.format("%s%s%s", System.getenv("HOME"), File.separator, dbFilesDir.substring(1));
        }
        System.err.println("Decoded db folder: " + dbFilesDir);
        File folder = new File(dbFilesDir);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                return;
        }
        File dbFile = new File(folder, dbFileName);
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        db = Nitrite.builder()
                .compressed()
                .filePath(dbFile.getAbsolutePath())
                .openOrCreate();
        collection = db.getCollection(collectionName);
        dbInitialized = true;
    }

    public int saveRecord(Aircraft aircraft) {
        return collection.insert(Document.createDocument(aircraft.key(), aircraft)).getAffectedCount();
    }

    public int batchSave(Collection<Aircraft> aircrafts) {
        Document[] array = new Document[aircrafts.size()];
        int i = 0;
        for (Aircraft aircraft : aircrafts) {
            array[i] = Document.createDocument(aircraft.key(), aircraft);
            i++;
        }
        return collection.insert(array).getAffectedCount();
    }

    public boolean isDbInitialized() {
        return dbInitialized;
    }
}

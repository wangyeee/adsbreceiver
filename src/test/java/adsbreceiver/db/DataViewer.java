package adsbreceiver.db;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.junit.jupiter.api.Test;

public class DataViewer {

    @Test
    public void testLoadData() {
        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath(System.getenv("HOME") + "/.adsbrec/rec.db")
                .openOrCreate();
        assertNotNull(db);
        NitriteCollection collection = db.getCollection("Aircrafts");
        assertNotNull(collection);
        Cursor cursor = collection.find();
        assertNotNull(cursor);
        for (Document document : cursor) {
            System.out.println(document);
        }
    }
}

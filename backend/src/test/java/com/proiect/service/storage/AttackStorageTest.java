package com.proiect.service.storage;

import com.proiect.model.AttackEvent;
import com.proiect.model.GeoLocation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Teste unitare pentru AttackStorage.
 */
public class AttackStorageTest {

    /**
     * Testează loadEvents când nu există fișier.
     */
    @Test
    public void loadEvents_noFile_returnsEmpty() throws IOException {
        Path tempPath = Files.createTempFile("test", ".json");
        AttackStorage storage = new AttackStorage(tempPath, null);
        List<AttackEvent> events = storage.loadEvents();
        assertTrue(events.isEmpty());
        Files.deleteIfExists(tempPath);
    }

    /**
     * Testează saveEvents cu evenimente goale.
     */
    @Test
    public void saveEvents_emptyList_doesNotThrow() throws IOException {
        Path tempPath = Files.createTempFile("test", ".json");
        AttackStorage storage = new AttackStorage(tempPath, null);
        assertDoesNotThrow(() -> storage.saveEvents(List.of()));
        Files.deleteIfExists(tempPath);
    }
}
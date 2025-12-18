package com.proiect.service.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proiect.model.AttackEvent;
import com.proiect.service.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AttackStorage {
    private final Path storagePath;
    private final Gson gson;

    /**
     * Construieste un AttackStorage cu calea data.
     * @param storagePath calea pentru stocarea evenimentelor
     */
    public AttackStorage(Path storagePath) {
        this.storagePath = storagePath;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Incarca evenimentele de atac din stocare.
     * @return lista de evenimente de atac
     */
    public List<AttackEvent> loadEvents() {
        if (!Files.exists(storagePath)) {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(storagePath)) {
            AttackEvent[] events = gson.fromJson(reader, AttackEvent[].class);
            if (events == null || events.length == 0) {
                return List.of();
            }
            return List.copyOf(Arrays.asList(events));
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Salveaza evenimentele de atac in stocare.
     * @param events lista de evenimente de atac
     */
    public void saveEvents(List<AttackEvent> events) {
        try {
            Files.createDirectories(storagePath.getParent());
            try (Writer writer = Files.newBufferedWriter(storagePath)) {
                gson.toJson(events, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

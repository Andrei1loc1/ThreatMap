package com.proiect.service.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.proiect.model.AttackEvent;
import com.proiect.service.utils.LocalDateTimeAdapter;
import com.proiect.entity.EvenimenteSecuritate;
import com.proiect.repository.EvenimenteSecuritateRepository;
import org.springframework.transaction.annotation.Transactional;

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
    private final EvenimenteSecuritateRepository repository;

    /**
     * Construiește un AttackStorage cu calea dată și repository-ul DB.
     *
     * @param storagePath calea pentru stocarea evenimentelor
     * @param repository repository-ul DB
     */
    public AttackStorage(Path storagePath, EvenimenteSecuritateRepository repository) {
        this.storagePath = storagePath;
        this.repository = repository;
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
     * Salveaza evenimentele de atac in stocare DB și JSON ca backup.
     * @param events lista de evenimente de atac
     */
    public void saveEvents(List<AttackEvent> events) {
        System.out.println("saveEvents: Saving " + events.size() + " events");
        // Save to DB
        for (AttackEvent event : events) {
            EvenimenteSecuritate dbEvent = new EvenimenteSecuritate();
            // Map AttackEvent to EvenimenteSecuritate (simplified mapping)
            // Create a fake log line that parseLine can handle
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");
            String timestamp = event.getDetectionTime().format(formatter);
            String fakeLog = timestamp + " host sshd[123]: Failed password for root from " + event.getAdresaIP() + " port 22 ssh2";
            dbEvent.setRawLog(fakeLog);
            dbEvent.setDataEveniment(event.getDetectionTime());
            dbEvent.setUserIncercat("unknown"); // Add logic to map properly
            try {
                repository.save(dbEvent);
                System.out.println("Saved event: " + dbEvent.getRawLog());
            } catch (Exception e) {
                System.err.println("Failed to save event: " + e.getMessage());
            }
        }

        // Backup to JSON
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

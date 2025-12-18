package com.proiect.controller;

import com.proiect.model.AttackEvent;
import com.proiect.model.LogEntry;
import com.proiect.service.detection.AttackDetector;
import com.proiect.service.detection.IpAnalysisService;
import com.proiect.service.storage.AttackStorage;
import com.proiect.service.parsing.LogParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AttackController{
    private final LogParser logParser;
    private final AttackDetector detector;
    private final AttackStorage storage;
    private final IpAnalysisService ipAnalysisService;
    private static final String DEFAULT_LOG_PATH = Paths.get("logs", "example.log").toAbsolutePath().toString();
    private final AtomicReference<List<AttackEvent>> latestAttacks = new AtomicReference<>(List.of());
    private final AtomicReference<List<LogEntry>> aggregatedEntries = new AtomicReference<>(List.of());

    /**
     * Construieste un AttackController cu parserul de log si serviciul de analiza IP dat.
     * @param logParser parserul de log
     * @param ipAnalysisService serviciul de analiza IP
     */
    @Autowired
    public AttackController(LogParser logParser, IpAnalysisService ipAnalysisService) {
        this.logParser = logParser;
        this.storage = new AttackStorage(Paths.get("logs", "latest-attacks.json"));
        this.ipAnalysisService = ipAnalysisService;
        this.detector = new AttackDetector(ipAnalysisService);

        List<AttackEvent> savedEvents = storage.loadEvents();
        if (!savedEvents.isEmpty()) {
            latestAttacks.set(savedEvents);
        } else {
            var defaultEntries = logParser.parseLogFile(DEFAULT_LOG_PATH);
            detectAndPersist(defaultEntries);
        }
        aggregatedEntries.set(logParser.parseLogFile(DEFAULT_LOG_PATH));
    }

    /**
     * Proceseaza fisierul de log si returneaza atacurile detectate.
     * @param filePath calea optionala a fisierului
     * @return lista de evenimente de atac
     */
    @GetMapping("/process")
    public List<AttackEvent> processLog(@RequestParam(required = false) String filePath) {
        String resolvedPath = (filePath == null || filePath.isBlank()) ? DEFAULT_LOG_PATH : filePath;
        var entries = logParser.parseLogFile(resolvedPath);
        return detectAndPersist(entries);
    }

    /**
     * Proceseaza fisierul de log incarcat si returneaza atacurile detectate.
     * @param file fisierul incarcat
     * @return lista de evenimente de atac
     */
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttackEvent> processUploadedLog(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return latestAttacks.get();
        }

        try (InputStream inputStream = file.getInputStream()) {
            var entries = logParser.parseInputStream(inputStream);
            return detectAndPersist(entries);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read uploaded log file", e);
        }
    }

    /**
     * Obtine ultimele atacuri detectate.
     * @return lista de evenimente de atac
     */
    @GetMapping("/attacks")
    public List<AttackEvent> getLatestAttacks() {
        return latestAttacks.get();
    }

    /**
     * Obtine statisticile sumare ale atacurilor.
     * @return harta statisticilor
     */
    @GetMapping("/stats/summary")
    public Map<String, Object> getSummaryStats() {
        List<AttackEvent> snapshot = latestAttacks.get();
        long uniqueIps = snapshot.stream()
                .map(AttackEvent::getAdresaIP)
                .distinct()
                .count();
        int totalAttempts = snapshot.stream()
                .mapToInt(AttackEvent::getFailedAttempts)
                .sum();

        String busiestCountry = snapshot.stream()
                .map(event -> event.getLocation() != null ? event.getLocation().getCountry() : "Unknown")
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        return Map.of(
                "totalEvents", snapshot.size(),
                "totalFailedAttempts", totalAttempts,
                "uniqueIps", uniqueIps,
                "busiestCountry", busiestCountry
        );
    }

    /**
     * Obtine IP-urile unice din atacuri.
     * @return lista de IP-uri unice
     */
    @GetMapping("/unique-ips")
    public List<String> getUniqueIps() {
        List<AttackEvent> snapshot = latestAttacks.get();
        return snapshot.stream()
                .map(AttackEvent::getAdresaIP)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Obtine procentul de risc prezis de AI.
     * @return harta cu procentul de pericol
     */
    @GetMapping("/stats/ai-risk")
    public Map<String, Integer> getAiRiskPercent() {
        List<LogEntry> entries = aggregatedEntries.get();
        if (entries.isEmpty()) {
            var defaultEntries = logParser.parseLogFile(DEFAULT_LOG_PATH);
            aggregatedEntries.set(defaultEntries);
            entries = defaultEntries;
        }

        int risk = detector.predictDangerPercent(entries);
        return Map.of("dangerPercent", risk);
    }

    /**
     * Detecteaza atacuri si le persista.
     * @param entries intrarile de log
     * @return lista de evenimente de atac
     */
    private List<AttackEvent> detectAndPersist(List<LogEntry> entries) {
        var current = aggregatedEntries.get();
        var combined = new java.util.ArrayList<LogEntry>(current.size() + entries.size());
        combined.addAll(current);
        combined.addAll(entries);
        aggregatedEntries.set(List.copyOf(combined));

        var events = detector.detectAttacks(entries);
        var snapshot = List.copyOf(events);
        latestAttacks.set(snapshot);
        storage.saveEvents(snapshot);
        return snapshot;
    }
}

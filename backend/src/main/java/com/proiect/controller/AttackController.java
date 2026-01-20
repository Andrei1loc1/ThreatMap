package com.proiect.controller;

import com.proiect.model.AttackEvent;
import com.proiect.model.LogEntry;
import com.proiect.model.GeoLocation;
import com.proiect.service.EmailService;
import com.proiect.service.detection.AttackDetector;
import com.proiect.service.detection.IpAnalysisService;
import com.proiect.service.storage.AttackStorage;
import com.proiect.service.parsing.LogParser;
import com.proiect.repository.EvenimenteSecuritateRepository;
import com.proiect.entity.EvenimenteSecuritate;
import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller REST pentru gestionarea detectării atacurilor, procesării log-urilor și operații conexe.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AttackController {
    private final LogParser logParser;
    private final AttackDetector detector;
    private final AttackStorage storage;
    private final IpAnalysisService ipAnalysisService;
    private final EmailService emailService;
    private final EvenimenteSecuritateRepository repository;
    private static final String DEFAULT_LOG_PATH = Paths.get("logs", "example.log").toAbsolutePath().toString();
    private final AtomicReference<List<AttackEvent>> latestAttacks = new AtomicReference<>(List.of());
    private final AtomicReference<List<LogEntry>> aggregatedEntries = new AtomicReference<>(List.of());
    private final ConcurrentLinkedQueue<AttackEvent> emailBuffer = new ConcurrentLinkedQueue<>();
    private volatile boolean emailConnected = false;
    private final Set<String> detectedIps = ConcurrentHashMap.newKeySet();

    /**
     * Construieste un AttackController cu parserul de log si serviciul de analiza IP dat.
     *
     * @param logParser         parserul de log
     * @param ipAnalysisService serviciul de analiza IP
     * @param emailService      serviciul de email
     * @param repository        repository-ul pentru evenimente
     */
    @Autowired
    public AttackController(LogParser logParser, IpAnalysisService ipAnalysisService, EmailService emailService, EvenimenteSecuritateRepository repository) {
        this.logParser = logParser;
        this.repository = repository;
        this.storage = new AttackStorage(Paths.get("logs", "latest-attacks.json"), repository);
        this.ipAnalysisService = ipAnalysisService;
        this.emailService = emailService;
        this.detector = new AttackDetector(ipAnalysisService);


        List<EvenimenteSecuritate> dbEvents = repository.findAll();
        System.out.println("Constructor: Loaded " + dbEvents.size() + " events from DB");
        if (!dbEvents.isEmpty()) {
            List<AttackEvent> dbAttackEvents = dbEvents.stream().map(dbEvent -> {
                String rawLog = dbEvent.getRawLog();
                String ip = "unknown";
                if (rawLog != null) {
                    if (rawLog.startsWith("Attack detected: ")) {
                        ip = rawLog.substring("Attack detected: ".length());
                    } else if (rawLog.contains("from ")) {
                        String[] parts = rawLog.split("from ");
                        if (parts.length > 1) {
                            ip = parts[1].split("\\s+")[0];
                        }
                    }
                }
                GeoLocation location = ipAnalysisService.analyzeIp(ip)
                        .map(data -> new GeoLocation(data.getCountry(), data.getCity(), data.getLatitudine(), data.getLongitudine()))
                        .orElse(new GeoLocation("Unknown", "Unknown", 0.0, 0.0));
                AttackEvent attackEvent = new AttackEvent(ip, 1, location);
                attackEvent.setDetectionTime(dbEvent.getDataEveniment());
                return attackEvent;
            }).collect(Collectors.toList());
            latestAttacks.set(dbAttackEvents);


            List<LogEntry> dbLogEntries = dbEvents.stream().map(dbEvent -> {
                try {
                    String raw = dbEvent.getRawLog();
                    System.out.println("Loading rawLog: " + raw);

                    if (raw.startsWith("Attack detected: ")) {
                        String ip = raw.substring("Attack detected: ".length());
                        raw = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd HH:mm:ss")) +
                                " host sshd[123]: Failed password for root from " + ip + " port 22 ssh2";
                        System.out.println("Converted to: " + raw);
                    }
                    LogEntry entry = logParser.parseLine(raw);
                    System.out.println("Parsed entry: " + entry);
                    return entry;
                } catch (IllegalArgumentException e) {
                    System.err.println("Failed to parse rawLog: " + dbEvent.getRawLog());
                    return null;
                }
            }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
            System.out.println("Loaded " + dbLogEntries.size() + " log entries from DB");
            if (!dbLogEntries.isEmpty()) {
                aggregatedEntries.set(dbLogEntries);
            }
        } else {

            List<AttackEvent> savedEvents = storage.loadEvents();
            if (!savedEvents.isEmpty()) {
                latestAttacks.set(savedEvents);
            } else {
                try {
                    var resource = new ClassPathResource("example.log");
                    var defaultEntries = logParser.parseInputStream(resource.getInputStream());
                    detectAndPersist(defaultEntries);
                } catch (IOException e) {
                    System.err.println("Failed to load default log file");
                    detectAndPersist(List.of());
                }
            }
        }
        try {
            var resource = new ClassPathResource("example.log");
            aggregatedEntries.set(logParser.parseInputStream(resource.getInputStream()));
        } catch (IOException e) {
            System.err.println("Failed to load default log file");
            aggregatedEntries.set(List.of());
        }
    }

    /**
     * Proceseaza fisierul de log si returneaza atacurile detectate.
     *
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
     *
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
     * Obtine ultimele atacuri detectate din DB.
     * @return lista de evenimente de atac
     */
    /**
     * Returnează ultimele atacuri detectate din baza de date.
     *
     * @return listă de obiecte AttackEvent
     */
    @GetMapping("/attacks")
    public List<AttackEvent> getLatestAttacks() {

        List<EvenimenteSecuritate> dbEvents = repository.findAll();
        return dbEvents.stream().map(dbEvent -> {

            String rawLog = dbEvent.getRawLog();
            String ip = "unknown";
            if (rawLog != null) {
                if (rawLog.startsWith("Attack detected: ")) {
                    ip = rawLog.substring("Attack detected: ".length());
                } else if (rawLog.contains("from ")) {
                    // Parse from fake log: "MMM dd HH:mm:ss host sshd[123]: Failed password for root from IP port 22 ssh2"
                    String[] parts = rawLog.split("from ");
                    if (parts.length > 1) {
                        ip = parts[1].split("\\s+")[0];
                    }
                }
            }
            int attempts = 4; // Set to 4 since detection requires >=4
            GeoLocation location = ipAnalysisService.analyzeIp(ip)
                    .map(data -> new GeoLocation(data.getCountry(), data.getCity(), data.getLatitudine(), data.getLongitudine()))
                    .orElse(new GeoLocation("Unknown", "Unknown", 0.0, 0.0));
            AttackEvent attackEvent = new AttackEvent(ip, attempts, location);
            attackEvent.setDetectionTime(dbEvent.getDataEveniment());
            return attackEvent;
        }).collect(Collectors.toList());
    }

    /**
     * Obtine statisticile sumare ale atacurilor.
     *
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
     * Obține IP-uri unice din evenimentele recente.
     *
     * @return listă de adrese IP unice
     */
    public List<String> getUniqueIps() {
        List<AttackEvent> snapshot = latestAttacks.get();
        List<String> ips = snapshot.stream()
                .map(AttackEvent::getAdresaIP)
                .distinct()
                .collect(Collectors.toList());
        System.out.println("getUniqueIps: latestAttacks size: " + snapshot.size() + ", unique IPs: " + ips + ", IPs list: " + snapshot.stream().map(AttackEvent::getAdresaIP).toList());
        return ips;
    }

    /**
     * Obtine procentul de risc prezis de AI.
     *
     * @return harta cu procentul de pericol
     */
    @PostMapping("/stats/ai-risk")
    public Map<String, Integer> getAiRiskPercent(@RequestBody List<String> logLines) {
        System.out.println("getAiRiskPercent: received " + logLines.size() + " log lines");
        List<LogEntry> entries = logLines.stream()
                .map(line -> {
                    try {
                        return logParser.parseLine(line);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid log line: " + line);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
        System.out.println("Parsed " + entries.size() + " valid entries");

        int risk = detector.predictDangerPercent(entries);
        System.out.println("Calculated risk: " + risk);
        return Map.of("dangerPercent", risk);
    }

    /**
     * Conecteaza un email pentru alerte.
     *
     * @param request corpul cererii cu email
     * @return raspunsul
     */
    @PostMapping("/connect-email")
    public ResponseEntity<String> connectEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        emailService.sendConnectionEmail(email);
        emailConnected = true;
        return ResponseEntity.ok("Email connected successfully");
    }

    @PostMapping("/ingest")
    public ResponseEntity<?> ingestLog(@RequestBody Map<String, Object> filebeatData) {
        String message = (String) filebeatData.get("message");
        if (message == null) {
            return ResponseEntity.badRequest().body("No message");
        }
        try {
            processLogMessage(message);
            return ResponseEntity.ok("Ingested");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Linie invalidă: " + e.getMessage());
        }
    }

    /**
     * Procesează un singur mesaj de log și detectează atacuri.
     *
     * @param message mesajul de log de procesat
     * @throws IllegalArgumentException dacă mesajul este invalid
     */
    public void processLogMessage(String message) throws IllegalArgumentException {
        LogEntry entry = logParser.parseLine(message);
        detectAndPersist(List.of(entry));
    }

    /**
     * Verifică dacă email-ul este conectat.
     *
     * @return true dacă email-ul este conectat
     */
    public boolean isEmailConnected() {
        return emailConnected;
    }

    /**
     * Returnează buffer-ul de evenimente pentru email.
     *
     * @return buffer-ul de evenimente
     */
    public ConcurrentLinkedQueue<AttackEvent> getEmailBuffer() {
        return emailBuffer;
    }

    /**
     * Resetează simularea: șterge toate datele din DB și resetează stările.
     */
    @DeleteMapping("/reset")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> resetSimulation() {
        repository.deleteAll();
        latestAttacks.set(List.of());
        aggregatedEntries.set(List.of());
        emailBuffer.clear();
        emailConnected = false;
        detectedIps.clear();
        System.out.println("Simulation reset: DB cleared, states reset");
        return ResponseEntity.ok("Simulation reset successfully");
    }

    /**
     * Trimite alerte email periodice pentru evenimente noi.
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void sendPeriodicEmailAlerts() {
        if (!emailConnected) {
            return;
        }

        if (emailBuffer.isEmpty()) {
            return;
        }

        List<AttackEvent> eventsToSend = List.copyOf(emailBuffer);
        emailBuffer.clear();

        if (!eventsToSend.isEmpty()) {
            StringBuilder html = new StringBuilder("<p>New Threats Detected:</p><ul>");
            for (AttackEvent event : eventsToSend) {
                html.append("<li>IP: ").append(event.getAdresaIP())
                        .append(", Attempts: ").append(event.getFailedAttempts())
                        .append("</li>");
            }
            html.append("</ul>");
            try {
                emailService.sendAlertEmail("chindrisandrei2005@gmail.com", html.toString());
            } catch (Exception e) {
                System.err.println("Failed to send periodic email alert: " + e.getMessage());
            }
        }
    }

    /**
     * Detectează atacuri din intrările de log și le persistă.
     *
     * @param entries intrările de log de analizat
     * @return listă de evenimente de atac detectate
     */
    private List<AttackEvent> detectAndPersist(List<LogEntry> entries) {
        var current = aggregatedEntries.get();
        var combined = new java.util.ArrayList<LogEntry>(current.size() + entries.size());
        combined.addAll(current);
        combined.addAll(entries);
        aggregatedEntries.set(List.copyOf(combined));

        var events = detector.detectAttacks(aggregatedEntries.get()); // Detect on all aggregated
        // Only add new IPs to avoid duplicates
        var newEvents = events.stream()
            .filter(event -> detectedIps.add(event.getAdresaIP()))
            .toList();
        // Accumulate all detected attacks
        var currentAttacks = latestAttacks.get();
        var combinedAttacks = new java.util.ArrayList<AttackEvent>(currentAttacks.size() + newEvents.size());
        combinedAttacks.addAll(currentAttacks);
        combinedAttacks.addAll(newEvents);
        latestAttacks.set(List.copyOf(combinedAttacks));
        try {
            storage.saveEvents(newEvents);
        } catch (Exception e) {
            System.err.println("Failed to save event: " + e.getMessage());
        }

        // Add to email buffer if new events
        if (!newEvents.isEmpty()) {
            emailBuffer.addAll(newEvents);
        }

        return newEvents;
    }
}
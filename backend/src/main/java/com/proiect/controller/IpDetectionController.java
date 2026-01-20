package com.proiect.controller;

import com.proiect.model.IpData;
import com.proiect.service.detection.IpAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Controller REST pentru operații de detectare și analiză IP.
 */
@RestController
@RequestMapping("/api/ip-detection")
@CrossOrigin(origins = "*")
public class IpDetectionController {
    @Autowired
    private IpAnalysisService ipAnalysisService;
    @Autowired
    private AttackController attackController;

    /**
     * Analizează IP-ul dat și returnează datele acestuia.
     *
     * @param ip adresa IP de analizat
     * @return entitate de răspuns cu date IP
     */
    @GetMapping("/analyze")
    public ResponseEntity<IpData> analyzeIp(@RequestParam String ip){
        var result = ipAnalysisService.analyzeIp(ip);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtine datele IP pentru IP-urile din log asincron.
     * @return completable future cu entitatea de raspuns
     */
    @GetMapping("/log-ips")
    public ResponseEntity<List<IpData>> getLogIps() {
        List<String> ips = attackController.getUniqueIps().stream().limit(10).toList(); // Limit to 10 IPs for performance
        System.out.println("getLogIps: Analyzing " + ips.size() + " IPs: " + ips);
        if (ips.isEmpty()) {
            System.out.println("getLogIps: No IPs to analyze");
            return ResponseEntity.ok(List.of());
        }
        List<IpData> results = ips.stream()
                .map(ip -> {
                    try {
                        Optional<IpData> data = ipAnalysisService.analyzeIp(ip);
                        System.out.println("getLogIps: Analyzed IP " + ip + " -> " + (data.isPresent() ? "success" : "failed"));
                        return data.orElse(null);
                    } catch (Exception e) {
                        System.err.println("getLogIps: Error analyzing IP " + ip + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        System.out.println("getLogIps: Completed analysis, results: " + results.size());
        return ResponseEntity.ok(results);
    }
}

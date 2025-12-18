package com.proiect.controller;

import com.proiect.model.IpData;
import com.proiect.service.detection.IpAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ip-detection")
@CrossOrigin(origins = "*")
public class IpDetectionController {
    @Autowired
    private IpAnalysisService ipAnalysisService;
    @Autowired
    private AttackController attackController;

    /**
     * Analizeaza IP-ul si returneaza datele.
     * @param ip adresa IP
     * @return entitatea de raspuns cu date IP
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
    public CompletableFuture<ResponseEntity<List<IpData>>> getLogIps() {
        List<String> ips = attackController.getUniqueIps();
        List<CompletableFuture<Optional<IpData>>> futures = ips.stream()
                .map(ip -> ipAnalysisService.analyzeIpAsync(ip))
                .collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(f -> f.join().orElse(null))
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList()))
                .thenApply(results -> ResponseEntity.ok(results));
    }
}

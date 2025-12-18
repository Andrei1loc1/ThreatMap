package com.proiect.service.detection;

import com.proiect.model.AttackEvent;
import com.proiect.model.GeoLocation;
import com.proiect.model.LogEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttackDetector {
    private final IpAnalysisService ipAnalysisService;
    private final AIPredictionAttack aiPredictor;

    /**
     * Construieste un AttackDetector cu serviciul de analiza IP dat.
     * @param ipAnalysisService serviciul pentru analiza IP
     */
    public AttackDetector(IpAnalysisService ipAnalysisService) {
        this.ipAnalysisService = ipAnalysisService;
        this.aiPredictor = new AIPredictionAttack();
    }

    /**
     * Detecteaza atacuri din intrarile de log date.
     * @param entries lista de intrari de log de analizat
     * @return o lista de evenimente de atac detectate
     */
    public List<AttackEvent> detectAttacks(List<LogEntry> entries) {
        if (!aiPredictor.isAttack(entries)) {
            return List.of();
        }

        Map<String, Long> failCountMap = entries.stream()
                .filter(entry -> "FAILURE".equalsIgnoreCase(entry.getStatus()))
                .collect(Collectors.groupingBy(LogEntry::getAdresaIP, Collectors.counting()));

        List<AttackEvent> detected = new ArrayList<>();
        for (Map.Entry<String, Long> entry : failCountMap.entrySet()) {
            detected.add(buildEvent(entry.getKey(), entry.getValue().intValue()));
        }

        return detected;
    }

    /**
     * Construieste un eveniment de atac pentru IP-ul si numarul de incercari dat.
     * @param ip adresa IP
     * @param attempts numarul de incercari esuate
     * @return evenimentul de atac construit
     */
    private AttackEvent buildEvent(String ip, int attempts) {
        GeoLocation location = ipAnalysisService.analyzeIp(ip).map(data -> new GeoLocation(data.getCountry(), data.getCity(), data.getLatitudine(), data.getLongitudine())).orElse(new GeoLocation());
        return new AttackEvent(ip, attempts, location);
    }

    /**
     * Prezice procentul de pericol bazat pe intrarile de log.
     * @param entries lista de intrari de log
     * @return procentul de pericol prezis
     */
    public int predictDangerPercent(List<LogEntry> entries) {
        return aiPredictor.predictDangerPercent(entries);
    }
}

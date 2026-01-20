package com.proiect.simulation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST pentru gestionarea operațiilor de simulare.
 */
@RestController
@RequestMapping("/api/simulator")
@CrossOrigin(origins = "*")
public class SimulationController {
    private final SimulationService simulationService;

    @Autowired
    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    /**
     * Pornește simularea cu parametrii dați.
     *
     * @param request corpul cererii cu duration, rate, attackPercent
     * @return răspuns care indică succes sau eroare
     */
    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> req = request != null ? request : Map.of();
        int duration;
        int rate;
        double attackPercent;
        try {
            duration = req.containsKey("duration") ? ((Number) req.get("duration")).intValue() : 60;
            rate = req.containsKey("rate") ? ((Number) req.get("rate")).intValue() : 1;
            attackPercent = req.containsKey("attackPercent") ? ((Number) req.get("attackPercent")).doubleValue() : 30.0;
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().body("Invalid parameter types");
        }
        simulationService.startSimulation(duration, rate, attackPercent);
        return ResponseEntity.ok("Simulation started");
    }

    /**
     * Oprește simularea în desfășurare.
     *
     * @return răspuns care indică succes
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        simulationService.stopSimulation();
        return ResponseEntity.ok("Simulation stopped");
    }

    /**
     * Obține statusul curent al simulării.
     *
     * @return hartă cu statusul de rulare
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus() {
        return ResponseEntity.ok(Map.of("running", simulationService.isSimulationRunning()));
    }
}
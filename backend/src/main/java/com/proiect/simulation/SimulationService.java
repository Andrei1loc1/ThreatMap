package com.proiect.simulation;

import com.proiect.controller.AttackController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Serviciu pentru gestionarea simulării atacurilor.
 */
@Service
public class SimulationService {
    private final AttackController attackController;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Autowired
    public SimulationService(AttackController attackController) {
        this.attackController = attackController;
    }

    /**
     * Pornește simularea pentru durata dată.
     *
     * @param durationSeconds durata în secunde
     * @param ratePerSecond rata de generare log-uri pe secundă
     * @param attackPercent procentul de evenimente de atac
     */
    public void startSimulation(int durationSeconds, int ratePerSecond, double attackPercent) {
        if (isRunning.get()) return;
        if (executor.isTerminated()) {
            executor = Executors.newScheduledThreadPool(1); // Recreate if terminated
        }
        isRunning.set(true);
        executor.scheduleAtFixedRate(() -> {
            if (!isRunning.get()) return;
            boolean isAttack = DataGenerator.isAttackEvent(attackPercent);
            if (isAttack) {
                // Generate 4 failed attempts for same IP
                java.util.List<String> attackIps = java.util.List.of("185.123.45.67", "203.0.113.1", "89.248.172.10");
                String attackIp = attackIps.get(new java.util.Random().nextInt(attackIps.size()));
                for (int i = 0; i < 4; i++) {
                    String logLine = DataGenerator.generateLogEntry(true, attackIp);
                    try {
                        attackController.processLogMessage(logLine);
                    } catch (IllegalArgumentException e) {
                        // Invalid log line, skip
                    }
                }
            } else {
                String logLine = DataGenerator.generateLogEntry(false);
                try {
                    attackController.processLogMessage(logLine);
                } catch (IllegalArgumentException e) {
                    // Invalid log line, skip
                }
            }
        }, 0, 1000 / ratePerSecond, TimeUnit.MILLISECONDS);

        // Oprește după durata
        executor.schedule(() -> stopSimulation(), durationSeconds, TimeUnit.SECONDS);
    }

    // Metoda pentru stop
    public void stopSimulation() {
        isRunning.set(false);
        executor.shutdown();
    }

    // Metoda pentru status
    public boolean isSimulationRunning() {
        return isRunning.get();
    }
}
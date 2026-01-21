package com.proiect.simulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitare pentru SimulationService.
 */
public class SimulationServiceTest {

    private final SimulationService service = new SimulationService(null);

    /**
     * Testează că simularea nu rulează inițial.
     */
    @Test
    public void isSimulationRunning_initiallyFalse() {
        assertFalse(service.isSimulationRunning());
    }
}
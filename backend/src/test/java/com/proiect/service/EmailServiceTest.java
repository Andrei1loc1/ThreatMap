package com.proiect.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitare pentru EmailService.
 */
public class EmailServiceTest {

    private final EmailService emailService = new EmailService();

    /**
     * Testează că sendAlertEmail nu aruncă eroare (folosește email verificat).
     */
    @Test
    public void sendAlertEmail_doesNotThrow() {
        assertDoesNotThrow(() -> emailService.sendAlertEmail("chindrisandrei2005@gmail.com", "Test message"));
    }
}
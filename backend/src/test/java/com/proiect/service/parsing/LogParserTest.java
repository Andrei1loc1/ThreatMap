package com.proiect.service.parsing;

import com.proiect.model.LogEntry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitare pentru LogParser.
 */
public class LogParserTest {

    private final LogParser logParser = new LogParser();

    /**
     * Testează parsarea unui log valid SSH.
     */
    @Test
    public void parseLine_validLog_returnsLogEntry() {
        String log = "Jan 20 22:22:33 host sshd[123]: Failed password for root from 203.0.113.1 port 22 ssh2";
        LogEntry entry = logParser.parseLine(log);
        assertEquals("203.0.113.1", entry.getAdresaIP());
        assertEquals("FAILURE", entry.getStatus());
    }

    /**
     * Testează parsarea unui log invalid.
     */
    @Test
    public void parseLine_invalidLog_throwsException() {
        String log = "invalid log";
        assertThrows(IllegalArgumentException.class, () -> logParser.parseLine(log));
    }
}
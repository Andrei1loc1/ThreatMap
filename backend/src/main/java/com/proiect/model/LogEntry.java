package com.proiect.model;

import java.time.LocalDateTime;

public class LogEntry {
    private final String adresaIP;
    private final LocalDateTime timestamp;
    private final String status;

    /**
     * Construieste un LogEntry.
     * @param adresaIP adresa IP
     * @param status statusul
     * @param timestamp timestamp-ul
     */
    public LogEntry(String adresaIP, String status, LocalDateTime timestamp) {
        this.adresaIP = adresaIP;
        this.status = status;
        this.timestamp = timestamp;
    }

    /**
     * Obtine adresa IP.
     * @return IP-ul
     */
    public String getAdresaIP() {
        return adresaIP;
    }
    /**
     * Obtine timestamp-ul.
     * @return timestamp-ul
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    /**
     * Obtine statusul.
     * @return statusul
     */
    public String getStatus() {
        return status;
    }
    /**
     * Returneaza reprezentarea sub forma de sir.
     * @return sirul
     */
    @Override
    public String toString() {
        return "LogEntry{" + "adresaIP='" + adresaIP + '\'' + ", timestamp=" + timestamp + ", status='" + status + '\'' + '}';
    }
}

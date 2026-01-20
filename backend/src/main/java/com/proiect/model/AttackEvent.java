package com.proiect.model;

import java.time.LocalDateTime;

public class AttackEvent {
    private String adresaIP;
    private int failedAttempts;
    private LocalDateTime detectionTime;
    private GeoLocation location;

    /**
     * Construieste un AttackEvent.
     * @param adresaIP adresa IP
     * @param failedAttempts numarul de incercari
     * @param location locatia
     */
    public AttackEvent(String adresaIP, int failedAttempts, GeoLocation location){
        this.adresaIP = adresaIP;
        this.failedAttempts = failedAttempts;
        this.location = location;
        detectionTime = LocalDateTime.now();
    }
    /**
     * Obtine adresa IP.
     * @return IP-ul
     */
    public String getAdresaIP() {
        return adresaIP;
    }
    /**
     * Obtine incercarile esuate.
     * @return incercarile
     */
    public int getFailedAttempts() {
        return failedAttempts;
    }
    /**
     * Obtine locatia.
     * @return locatia
     */
    public GeoLocation getLocation() {
        return location;
    }
    /**
     * Obtine timpul de detectie.
     * @return timpul de detectie
     */
    public LocalDateTime getDetectionTime() {
        return detectionTime;
    }
    /**
     * Seteaza timpul de detectie.
     * @param detectionTime timpul de detectie
     */
    public void setDetectionTime(LocalDateTime detectionTime) {
        this.detectionTime = detectionTime;
    }
    /**
     * Returneaza reprezentarea sub forma de sir.
     * @return sirul
     */
    @Override
    public String toString(){
        return "AttackEvent{" + "adresaIP='" + adresaIP + '\'' + ", failedAttempts=" + failedAttempts + ", location=" + location + ", detectionTime=" + detectionTime + '}';
    }

}

package com.proiect.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoLocation {
    private String country;
    private String city;
    @JsonProperty("latitudine")
    private double latitudine;
    @JsonProperty("longitudine")
    private double longitudine;

    /**
     * Construieste o GeoLocation goala.
     */
    public GeoLocation() {
        this.country = "Unknown";
        this.city = "Unknown";
        this.latitudine = 0.0;
        this.longitudine = 0.0;
    }

    /**
     * Construieste o GeoLocation cu campuri.
     * @param country tara
     * @param city oras
     * @param latitudine latitudinea
     * @param longitudine longitudinea
     */
    public GeoLocation(String country, String city, double latitudine, double longitudine) {
        this.country = country;
        this.city = city;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    /**
     * Obtine tara.
     * @return tara
     */
    public String getCountry() {
        return country;
    }

    /**
     * Seteaza tara.
     * @param country tara
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Obtine orasul.
     * @return orasul
     */
    public String getCity() {
        return city;
    }

    /**
     * Seteaza orasul.
     * @param city orasul
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Obtine latitudinea.
     * @return latitudinea
     */
    public double getLatitudine() {
        return latitudine;
    }

    /**
     * Seteaza latitudinea.
     * @param latitudine latitudinea
     */
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    /**
     * Obtine longitudinea.
     * @return longitudinea
     */
    public double getLongitudine() {
        return longitudine;
    }

    /**
     * Seteaza longitudinea.
     * @param longitudine longitudinea
     */
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }
}

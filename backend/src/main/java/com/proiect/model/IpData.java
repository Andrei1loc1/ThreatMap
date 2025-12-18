package com.proiect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
public class IpData extends GeoLocation {
    @JsonProperty("ip")
    private final String ip;
    @JsonProperty("isp")
    private final String isp;
    @JsonProperty("org")
    private final String org;
    @JsonProperty("isVpn")
    private boolean isVpn;
    /**
     * Construieste un IpData gol.
     */
    public IpData() {
        super();
        this.ip = null;
        this.isp = null;
        this.org = null;
        this.isVpn = false;
    }
    /**
     * Construieste un IpData cu toate campurile.
     * @param ip IP-ul
     * @param country tara
     * @param city oras
     * @param lat latitudinea
     * @param lon longitudinea
     * @param isp ISP-ul
     * @param org organizatia
     * @param isVpn daca este VPN
     */
    public IpData(String ip, String country, String city, double lat, double lon, String isp, String org, boolean isVpn) {
        super(country, city, lat, lon);
        this.ip = ip;
        this.isp = isp;
        this.org = org;
        this.isVpn = isVpn;
    }
    /**
     * Obtine IP-ul.
     * @return IP-ul
     */
    public String getIp() { return ip; }
    /**
     * Obtine ISP-ul.
     * @return ISP-ul
     */
    public String getIsp() { return isp; }
    /**
     * Obtine organizatia.
     * @return organizatia
     */
    public String getOrg() { return org; }
    /**
     * Verifica daca este VPN.
     * @return true daca este VPN
     */
    public boolean isVpn() { return isVpn; }
    /**
     * Seteaza indicatorul VPN.
     * @param isVpn indicatorul VPN
     */
    public void setIsVpn(boolean isVpn) { this.isVpn = isVpn; }
    /**
     * Returneaza reprezentarea sub forma de sir.
     * @return sirul
     */
    @Override
    public String toString() {
        return super.toString() + ", IpData{ip='%s', isp='%s', org='%s', isVpn=%b}".formatted(ip, isp, org, isVpn);
    }
}

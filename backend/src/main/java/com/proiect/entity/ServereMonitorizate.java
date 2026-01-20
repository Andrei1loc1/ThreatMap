package com.proiect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "servere_monitorizate")
public class ServereMonitorizate {

    @Id
    @Column(name = "server_id")
    private Long serverId;

    @Column(name = "nume_server", length = 50, nullable = false)
    private String numeServer;

    @Column(name = "adresa_ip", length = 15, nullable = false)
    private String adresaIp;

    @Column(name = "locatie", length = 50)
    private String locatie;

    @Column(name = "sistem_operare", length = 30)
    private String sistemOperare;

    @Column(name = "data_instalare")
    private LocalDate dataInstalare;

    @Column(name = "status_activ")
    private Integer statusActiv;

    // Constructors, getters, setters
    public ServereMonitorizate() {}

    public ServereMonitorizate(Long serverId, String numeServer, String adresaIp, String locatie, String sistemOperare, LocalDate dataInstalare, Integer statusActiv) {
        this.serverId = serverId;
        this.numeServer = numeServer;
        this.adresaIp = adresaIp;
        this.locatie = locatie;
        this.sistemOperare = sistemOperare;
        this.dataInstalare = dataInstalare;
        this.statusActiv = statusActiv;
    }

    // Getters and Setters
    public Long getServerId() { return serverId; }
    public void setServerId(Long serverId) { this.serverId = serverId; }

    public String getNumeServer() { return numeServer; }
    public void setNumeServer(String numeServer) { this.numeServer = numeServer; }

    public String getAdresaIp() { return adresaIp; }
    public void setAdresaIp(String adresaIp) { this.adresaIp = adresaIp; }

    public String getLocatie() { return locatie; }
    public void setLocatie(String locatie) { this.locatie = locatie; }

    public String getSistemOperare() { return sistemOperare; }
    public void setSistemOperare(String sistemOperare) { this.sistemOperare = sistemOperare; }

    public LocalDate getDataInstalare() { return dataInstalare; }
    public void setDataInstalare(LocalDate dataInstalare) { this.dataInstalare = dataInstalare; }

    public Integer getStatusActiv() { return statusActiv; }
    public void setStatusActiv(Integer statusActiv) { this.statusActiv = statusActiv; }
}
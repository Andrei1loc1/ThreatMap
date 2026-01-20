package com.proiect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "actori_amenintare")
public class ActoriAmenintare {

    @Id
    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "adresa_ip", length = 45, unique = true, nullable = false)
    private String adresaIp;

    @Column(name = "tara_origine", length = 50)
    private String taraOrigine;

    @Column(name = "isp_name", length = 100)
    private String ispName;

    @Column(name = "reputatie_ip")
    private Integer reputatieIp;

    // Constructors, getters, setters
    public ActoriAmenintare() {}

    public ActoriAmenintare(Long actorId, String adresaIp, String taraOrigine, String ispName, Integer reputatieIp) {
        this.actorId = actorId;
        this.adresaIp = adresaIp;
        this.taraOrigine = taraOrigine;
        this.ispName = ispName;
        this.reputatieIp = reputatieIp;
    }

    // Getters and Setters
    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getAdresaIp() { return adresaIp; }
    public void setAdresaIp(String adresaIp) { this.adresaIp = adresaIp; }

    public String getTaraOrigine() { return taraOrigine; }
    public void setTaraOrigine(String taraOrigine) { this.taraOrigine = taraOrigine; }

    public String getIspName() { return ispName; }
    public void setIspName(String ispName) { this.ispName = ispName; }

    public Integer getReputatieIp() { return reputatieIp; }
    public void setReputatieIp(Integer reputatieIp) { this.reputatieIp = reputatieIp; }
}
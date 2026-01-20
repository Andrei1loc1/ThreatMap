package com.proiect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipuri_atac")
public class TipuriAtac {

    @Id
    @Column(name = "tip_id")
    private Long tipId;

    @Column(name = "cod_atac", length = 20, unique = true)
    private String codAtac;

    @Column(name = "descriere", length = 200)
    private String descriere;

    @Column(name = "severitate")
    private Integer severitate;

    // Constructors, getters, setters
    public TipuriAtac() {}

    public TipuriAtac(Long tipId, String codAtac, String descriere, Integer severitate) {
        this.tipId = tipId;
        this.codAtac = codAtac;
        this.descriere = descriere;
        this.severitate = severitate;
    }

    // Getters and Setters
    public Long getTipId() { return tipId; }
    public void setTipId(Long tipId) { this.tipId = tipId; }

    public String getCodAtac() { return codAtac; }
    public void setCodAtac(String codAtac) { this.codAtac = codAtac; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public Integer getSeveritate() { return severitate; }
    public void setSeveritate(Integer severitate) { this.severitate = severitate; }
}
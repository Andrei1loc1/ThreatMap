package com.proiect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entitate care reprezintă evenimente de securitate în baza de date.
 */
@Entity
@Table(name = "evenimente_securitate")
public class EvenimenteSecuritate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private ServereMonitorizate server;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private ActoriAmenintare actor;

    @ManyToOne
    @JoinColumn(name = "tip_id")
    private TipuriAtac tip;

    @Column(name = "raw_log", length = 500)
    private String rawLog;

    @Column(name = "data_eveniment")
    private LocalDateTime dataEveniment;

    @Column(name = "user_incercat", length = 50)
    private String userIncercat;

    // Constructors, getters, setters
    public EvenimenteSecuritate() {}

    public EvenimenteSecuritate(Long eventId, ServereMonitorizate server, ActoriAmenintare actor, TipuriAtac tip, String rawLog, LocalDateTime dataEveniment, String userIncercat) {
        this.eventId = eventId;
        this.server = server;
        this.actor = actor;
        this.tip = tip;
        this.rawLog = rawLog;
        this.dataEveniment = dataEveniment;
        this.userIncercat = userIncercat;
    }

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public ServereMonitorizate getServer() { return server; }
    public void setServer(ServereMonitorizate server) { this.server = server; }

    public ActoriAmenintare getActor() { return actor; }
    public void setActor(ActoriAmenintare actor) { this.actor = actor; }

    public TipuriAtac getTip() { return tip; }
    public void setTip(TipuriAtac tip) { this.tip = tip; }

    public String getRawLog() { return rawLog; }
    public void setRawLog(String rawLog) { this.rawLog = rawLog; }

    public LocalDateTime getDataEveniment() { return dataEveniment; }
    public void setDataEveniment(LocalDateTime dataEveniment) { this.dataEveniment = dataEveniment; }

    public String getUserIncercat() { return userIncercat; }
    public void setUserIncercat(String userIncercat) { this.userIncercat = userIncercat; }
}
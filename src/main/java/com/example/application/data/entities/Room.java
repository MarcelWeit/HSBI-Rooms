package com.example.application.data.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Room{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String refNr;

    private Raumtyp typ;
    private int capacity;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "room_ausstattung",
            joinColumns = @JoinColumn(name = "room_refNr"),
            inverseJoinColumns = @JoinColumn(name = "ausstattung_id"))
    private Set<Ausstattung> ausstattung = new HashSet<>();
    private Fachbereich fachbereich;
    private String position;

    public Room() {
        // Empty constructor is needed by Spring Data / JPA
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRefNr() {
        return refNr;
    }

    public void setRefNr(String refNr) {
        // Capitalize the first letter of the reference number
        this.refNr = refNr.substring(0, 1).toUpperCase() + refNr.substring(1);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<Ausstattung> getAusstattung() {
        return ausstattung;
    }

    public void setAusstattung(Set<Ausstattung> ausstattung) {
        this.ausstattung = ausstattung;
    }

    public void removeAusstattung(Ausstattung ausstattung) {
        this.ausstattung.remove(ausstattung);
    }

    public void addAusstattung(Ausstattung ausstattung) {
        this.ausstattung.add(ausstattung);
    }

    public Raumtyp getTyp() {
        return typ;
    }

    public void setTyp(Raumtyp typ) {
        this.typ = typ;
    }

    public Fachbereich getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }

}


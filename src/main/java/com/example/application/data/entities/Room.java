package com.example.application.data.entities;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Room {

    @Id
    private String refNr;

    private String typ;
    private int capacity;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Ausstattung> ausstattung;
    private String fachbereich;

    public Room() {
        // Empty constructor is needed by Spring Data / JPA
    }

    public Room(int capacity, Set<Ausstattung> ausstattung, String refNr, String typ, String fachbereich) {
        this.capacity = capacity;
        this.ausstattung = ausstattung;
        this.refNr = refNr;
        this.typ = typ;
        this.fachbereich = fachbereich;
    }

    public String getRefNr() {
        return refNr;
    }

    public void setRefNr(String refNr) {
        this.refNr = refNr;
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

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(String fachbereich) {
        this.fachbereich = fachbereich;
    }

}


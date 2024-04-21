package com.example.application.data.entities;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Set;

@Entity
public class Room extends AbstractEntity {

    @GeneratedValue
    @Id
    private long id;

    private String typ;
    private String name;
    private int capacity;
    private String location;
    @ElementCollection
    private Set<String> ausstattung;
    private String fachbereich;
    private String refNr;

    public Room() {
        // Empty constructor is needed by Spring Data / JPA
    }

    public Room(String name, int capacity, String location, Set<String> ausstattung, String refNr, String typ, String fachbereich) {
        this.name = name;
        this.typ = typ;
        this.capacity = capacity;
        this.location = location;
        this.ausstattung = ausstattung;
        this.refNr = refNr;
        this.fachbereich = fachbereich;
    }

    public String getRefNr() {
        return refNr;
    }

    public void setRefNr(String refNr) {
        this.refNr = refNr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<String> getAusstattung() {
        return ausstattung;
    }

    public void setAusstattung(Set<String> ausstattung) {
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


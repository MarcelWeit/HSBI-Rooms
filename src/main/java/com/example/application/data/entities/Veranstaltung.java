package com.example.application.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Veranstaltung {

    @Id
    private String id;

    private String bezeichnung;
    @OneToOne
    private Dozent dozent;
    private int teilnehmerzahl;
    private Fachbereich fachbereich;

    public Veranstaltung() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Dozent getDozent() {
        return dozent;
    }

    public void setDozent(Dozent dozent) {
        this.dozent = dozent;
    }

    public int getTeilnehmerzahl() {
        return teilnehmerzahl;
    }

    public void setTeilnehmerzahl(int teilnehmerzahl) {
        this.teilnehmerzahl = teilnehmerzahl;
    }

    public Fachbereich getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }


}

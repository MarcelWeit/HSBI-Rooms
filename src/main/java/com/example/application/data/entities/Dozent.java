package com.example.application.data.entities;

import jakarta.persistence.*;


@Entity
@Table(name = "lecturer")
public class Dozent {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String nachname;
    private String vorname;
    private Fachbereich fachbereich;

    // Konstruktor
    public Dozent() {
    }

    public Dozent(String nachname, String vorname, Fachbereich fachbereich) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.fachbereich = fachbereich;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getters und Setters
    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public Fachbereich getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }

    @Override
    public String toString() {
        return nachname + ", " + vorname;
    }
}

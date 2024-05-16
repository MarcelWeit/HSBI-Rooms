package com.example.application.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import java.util.Set;


@Entity
@Table(name = "lecturer")
public class Dozent {
  
    @Id
    private long id;
  
    private String nachname;
    private String vorname;

    private Fachbereich fachbereich;

    // Konstruktor
    public Dozent() {
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
}

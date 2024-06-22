package com.example.application.data.entities;

import com.example.application.data.enums.Anrede;
import com.example.application.data.enums.Fachbereich;
import jakarta.persistence.*;

/**
 * Dozent Entity
 *
 * @author Gabriel Greb
 */

@Entity
@Table(name = "lecturer")
public class Dozent {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private Anrede anrede;
    private String nachname;
    private String vorname;
    private Fachbereich fachbereich;
    private String akad_titel;

    // Konstruktor
    public Dozent() {
    }

    public Dozent(Anrede anrede, String nachname, String vorname, Fachbereich fachbereich, String akad_titel) {
        this.anrede = anrede;
        this.nachname = nachname;
        this.vorname = vorname;
        this.fachbereich = fachbereich;
        this.akad_titel = akad_titel;
    }

    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
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

    public String getAkad_titel() {
        return akad_titel;
    }

    public void setAkad_titel(String akad_titel) {
        this.akad_titel = akad_titel;
    }

    @Override
    public String toString() {
        return nachname + ", " + vorname;
    }
}

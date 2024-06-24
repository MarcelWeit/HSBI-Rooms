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
    private String akadTitel;

    // Konstruktor
    public Dozent() {
    }

    public Dozent(Anrede anrede, String nachname, String vorname, Fachbereich fachbereich, String akadTitel) {
        this.anrede = anrede;
        this.nachname = nachname;
        this.vorname = vorname;
        this.fachbereich = fachbereich;
        this.akadTitel = akadTitel;
    }

    public Dozent(String nachname, String vorname, Fachbereich fachbereich) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.fachbereich = fachbereich;
    }

    public Dozent(String nachname, String vorname, Fachbereich fachbereich) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.fachbereich = fachbereich;
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

    public String getAkadTitel() {
        return akadTitel;
    }

    public void setAkadTitel(String akadTitel) {
        this.akadTitel = akadTitel;
    }

    @Override
    public String toString() {
        return anrede + " " + akadTitel + " " + vorname + " " + nachname;
    }
}

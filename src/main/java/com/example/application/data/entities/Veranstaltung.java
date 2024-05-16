package com.example.application.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Veranstaltung {

    @Id
    private String id;

    private String bezeichnung;
    private Dozent dozent; //Placeholder bis Klasse Dozent
    private int teilnehmerzahl;
    private Fachbereich fachbereich;

    public Veranstaltung() {}

    public String getId() {return id;}
    public String getBezeichnung() {return bezeichnung;}
    public String getDozent() {return dozent;}
    public int getTeilnehmerzahl() {return teilnehmerzahl;}
    public Fachbereich getFachbereich() {return fachbereich;}

    public void setId(String id) {this.id = id;}
    public void setBezeichnung(String bezeichnung) {this.bezeichnung = bezeichnung;}
    public void setDozent(String dozent) {this.dozent = dozent;}
    public void setTeilnehmerzahl(int teilnehmerzahl) {this.teilnehmerzahl = teilnehmerzahl;}
    public void setFachbereich(Fachbereich fachbereich) {this.fachbereich = fachbereich;}



}

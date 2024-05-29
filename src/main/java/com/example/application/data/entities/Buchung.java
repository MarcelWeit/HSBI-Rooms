package com.example.application.data.entities;

import com.example.application.data.enums.Zeitslot;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Buchung {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDate date;
    //    private LocalTime startZeit;
    //    private LocalTime endZeit;
    private Zeitslot zeitslot;

    @ManyToOne(fetch = FetchType.EAGER)
    private Raum room;

    @ManyToOne(fetch = FetchType.EAGER)
    private Veranstaltung veranstaltung;

    @ManyToOne(fetch = FetchType.EAGER)
    private Dozent dozent;

    public Buchung() {
    }

    public Buchung(Buchung buchung) {
        this.date = buchung.getDate();
        //        this.startZeit = buchung.getStartZeit();
        //        this.endZeit = buchung.getEndZeit();
        this.zeitslot = buchung.getZeitslot();
        this.room = buchung.getRoom();
        this.veranstaltung = buchung.getVeranstaltung();
        this.dozent = buchung.getDozent();
    }

    public long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Zeitslot getZeitslot() {
        return zeitslot;
    }

    public void setZeitslot(Zeitslot zeitslot) {
        this.zeitslot = zeitslot;
    }

    public Raum getRoom() {
        return room;
    }

    public void setRoom(Raum room) {
        this.room = room;
    }

    public Veranstaltung getVeranstaltung() {
        return veranstaltung;
    }

    public void setVeranstaltung(Veranstaltung veranstaltung) {
        this.veranstaltung = veranstaltung;
    }

    public Dozent getDozent() {
        return dozent;
    }

    public void setDozent(Dozent dozent) {
        this.dozent = dozent;
    }

    @Override
    public String toString() {
        return "Buchung{" +
                "date=" + date +
                ", zeitslot=" + zeitslot +
                ", room=" + room +
                ", veranstaltung=" + veranstaltung +
                ", dozent=" + dozent +
                '}';
    }
}

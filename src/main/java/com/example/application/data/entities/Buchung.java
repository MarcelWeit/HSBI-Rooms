package com.example.application.data.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Buchung {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDate date;
    private LocalTime startZeit;
    private LocalTime endZeit;

    @ManyToOne(fetch = FetchType.EAGER)
    private Raum room;

    @ManyToOne(fetch = FetchType.EAGER)
    private Veranstaltung veranstaltung;

    @ManyToOne(fetch = FetchType.EAGER)
    private Dozent dozent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartZeit() {
        return startZeit;
    }

    public void setStartZeit(LocalTime startZeit) {
        this.startZeit = startZeit;
    }

    public LocalTime getEndZeit() {
        return endZeit;
    }

    public void setEndZeit(LocalTime endZeit) {
        this.endZeit = endZeit;
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
                "id=" + id +
                ", date=" + date +
                ", startZeit=" + startZeit +
                ", endZeit=" + endZeit +
                ", room=" + room +
                ", veranstaltung=" + veranstaltung +
                ", dozent=" + dozent +
                '}';
    }
}

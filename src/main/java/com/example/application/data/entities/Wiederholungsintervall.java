package com.example.application.data.entities;

public enum Wiederholungsintervall {
    EINMALIG("Einmalig"),
    TÄGLICH("Täglich"),
    WÖCHENTLICH("Wöchentlich"),
    JÄHRLICH("Jährlich"),
    JEDENMOBISFR("Jeden Mo bis Fr"),
    MONATLICHAMERSTENTAGX("Monatlich am ersten Tag X");

    private final String anzeigeName;

    private Wiederholungsintervall(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    @Override
    public String toString() {
        return anzeigeName;
    }
}

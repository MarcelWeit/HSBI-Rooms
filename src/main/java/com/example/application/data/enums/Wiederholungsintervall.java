package com.example.application.data.enums;

/**
 * Aufzählung der Wiederholungsintervalle
 *
 * @author Marcel Weithoener
 */
public enum Wiederholungsintervall {
    EINMALIG("Einmalig"),
    TAEGLICH("Täglich"),
    WOECHENTLICH("Wöchentlich"),
    //    Monatlich("Monatlich"),
    JAEHRLICH("Jährlich");
    //    JEDENMOBISFR("Jeden Mo bis Fr"),
    //    MONATLICHAMERSTENTAGX("Monatlich am ersten Tag X");

    private final String anzeigeName;

    Wiederholungsintervall(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    @Override
    public String toString() {
        return anzeigeName;
    }
}

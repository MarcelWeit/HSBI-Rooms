package com.example.application.data.enums;

/**
 * Aufzählung der Fachbereiche
 *
 * @author Marcel Weithoener
 */

public enum Fachbereich {
    WIRTSCHAFT("Wirtschaft"),
    GESTALTUNG("Gestaltung"),
    SOZIALWESEN("Sozialwesen"),
    INGENIEURWISSENSCHAFTENUNDMATHEMATIK("Ingenieurwissenschaften und Mathematik"),
    GESUNDHEIT("Gesundheit"),
    CAMPUSMINDEN("Campus Minden"),
    CAMPUSGUETERSLOH("Campus Gütersloh");

    private final String anzeigeName;

    Fachbereich(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    public String toString() {
        return anzeigeName;
    }
}

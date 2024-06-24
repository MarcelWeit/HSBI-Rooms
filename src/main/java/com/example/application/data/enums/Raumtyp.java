package com.example.application.data.enums;

/**
 * Aufzählung der Raumtypen
 *
 * @author Marcel Weithoener
 */
public enum Raumtyp {
    SEMINARRAUM("Seminarraum"),
    HOERSAAL("Hörsaal"),
    RECHNERRAUM("Rechnerraum"),
    BESPRECHUNGSRAUM("Besprechungsraum"),
    LABOR("Labor"),
    KONFERENZRAUM("Konferenzraum");

    private final String anzeigeName;

    Raumtyp(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    public String toString() {
        return anzeigeName;
    }
}

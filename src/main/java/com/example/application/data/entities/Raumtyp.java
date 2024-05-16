package com.example.application.data.entities;

/**
 * @author marcel weithoener
 */
public enum Raumtyp {
    SEMINARRAUM("Seminarraum"),
    HOERSAAL("Hörsaal"),
    RECHNERRAUM("Rechnerraum"),
    BESPRECHUNGSRAUM("Besprechungsraum");

    private final String anzeigeName;

    Raumtyp(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    public String toString(){
        return anzeigeName;
    }
}

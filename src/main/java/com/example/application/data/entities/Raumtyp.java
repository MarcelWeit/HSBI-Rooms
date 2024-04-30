package com.example.application.data.entities;

public enum Raumtyp {
    SEMINARRAUM("Seminarraum"),
    HOERSAAL("Hörsaal"),
    RECHNERRAUM("Rechnerraum"),
    BESPRECHUNGSRAUM("Besprechungsraum");

    private String anzeigeName;

    Raumtyp(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    public String getAnzeigeName() {
        return anzeigeName;
    }
}

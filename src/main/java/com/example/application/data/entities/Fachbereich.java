package com.example.application.data.entities;

public enum Fachbereich {
    WIRTSCHAFT("Wirtschaft"),
    GESTALTUNG("Gestaltung"),
    SOZIALWESEN("Sozialwesen"),
    INGENIEURWISSENSCHAFTENUNDMATHEMATIK("Ingenieurwissenschaften und Mathematik"),
    GESUNDHEIT("Gesundheit"),
    CAMPUSMINDEN("Campus Minden"),
    CAMPUSGÜTERSLOH("Campus Gütersloh");

    private String anzeigeName;

    Fachbereich(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    public String getAnzeigeName() {
        return anzeigeName;
    }
}

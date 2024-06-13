package com.example.application.data.enums;

public enum Anrede {
    HERR("Herr"),
    FRAU("Frau");

    private final String anrede;

    Anrede(String anrede) {
        this.anrede = anrede;
    }

    public String toString() {
        return anrede;
    }
}

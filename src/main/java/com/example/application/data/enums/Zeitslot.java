package com.example.application.data.enums;

/**
 * Aufzählung der Zeitslots
 *
 * @author Marcel Weithoener
 */
public enum Zeitslot {
    EINS("08:00 - 09:30"),
    ZWEI("09:45 - 11:15"),
    DREI("11:30 - 13:00"),
    VIER("14:00 - 15:30"),
    FUENF("15:45 - 17:15"),
    SECHS("17:30 - 19:00"),
    SIEBEN("19:15 - 20:45");

    private final String anzeigeName;

    Zeitslot(String anzeigeName) {
        this.anzeigeName = anzeigeName;
    }

    @Override
    public String toString() {
        return anzeigeName;
    }
}

package com.example.application.data.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import java.util.Set;

@Entity
@Table(name = "lecturer")
public class Dozent extends AbstractEntity {
    private String username;
    private String nachname;
    private String vorname;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Fachbereich> fachbereich;

    // Konstruktor
    public Dozent() {
    }

    // Getters und Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Fachbereich> getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Set<Fachbereich> fachbereich) {
        this.fachbereich = fachbereich;
    }
}

package com.example.application.data.entities;

import com.example.application.data.enums.Anrede;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;

/**
 * Registrierung Entity, wird für Registrierungen verwendet, für die noch kein User angelegt werden soll.
 * Registrierungen können so in einer extra Tabelle gespeichert werden.
 *
 * @author Marcel Weithoener
 */
@Entity
public class Registrierung {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Email
    private String username;

    private String lastName;
    private String firstName;

    @JsonIgnore
    private String hashedPassword;

    private Role role;

    private Fachbereich fachbereich;

    private Anrede anrede;
    private String akadTitel;

    public Registrierung() {
    }

    public Registrierung(String username, String lastName, String firstName, String hashedPassword, Role role, Fachbereich fachbereich, Anrede anrede, String akadTitel) {
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.fachbereich = fachbereich;
        this.anrede = anrede;
        this.akadTitel = akadTitel;
    }

    public long getId() {
        return id;
    }

    // auf id setter wird verzichtet, da id automatisch generiert wird

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Fachbereich getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }

    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
    }

    public String getAkadTitel() {
        return akadTitel;
    }

    public void setAkadTitel(String akadTitel) {
        this.akadTitel = akadTitel;
    }
}

package com.example.application.data.entities;

import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Raumtyp;
import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Raum Entity
 *
 * @author Marcel Weithoener
 */
@Entity
public class Raum {

    @Id
    private String refNr;

    private Raumtyp typ;

    private int capacity;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "raum_ausstattung",
            joinColumns = @JoinColumn(name = "raum_refNr"),
            inverseJoinColumns = @JoinColumn(name = "ausstattung_id"))
    private Set<Ausstattung> ausstattung = new HashSet<>();

    private Fachbereich fachbereich;

    private String position;

    public Raum() {

    }

    public Raum(String refNr, Raumtyp typ, int capacity, Fachbereich fachbereich, String position) {
        this.refNr = refNr;
        this.typ = typ;
        this.capacity = capacity;
        this.fachbereich = fachbereich;
        this.position = position;
    }

    public String getRefNr() {
        return refNr;
    }

    public void setRefNr(String refNr) {
        // Capitalize the first letter of the reference number
        this.refNr = refNr.substring(0, 1).toUpperCase() + refNr.substring(1);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<Ausstattung> getAusstattung() {
        return ausstattung;
    }

    public void setAusstattung(Set<Ausstattung> ausstattung) {
        this.ausstattung = ausstattung;
    }

    public void removeAusstattung(Ausstattung ausstattung) {
        this.ausstattung.remove(ausstattung);
    }

    public void addAusstattung(Ausstattung ausstattung) {
        if (ausstattung != null) {
            this.ausstattung.add(ausstattung);
        }
    }

    public Raumtyp getTyp() {
        return typ;
    }

    public void setTyp(Raumtyp typ) {
        this.typ = typ;
    }

    public Fachbereich getFachbereich() {
        return fachbereich;
    }

    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAusstattungAsString() {
        if (ausstattung.isEmpty()) {
            return "Keine Ausstattung";
        }
        return ausstattung.stream()
                .map(Ausstattung::getBez)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return typ.toString() + " " + refNr;
    }

    // equals und hashCode von JPABuddy generiert
    // durch andere Implementationen können Probleme bei der Verwendung von Hibernate / SpringBoot entstehen
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Raum raum = (Raum) o;
        return getRefNr() != null && Objects.equals(getRefNr(), raum.getRefNr());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}


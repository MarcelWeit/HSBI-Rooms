package com.example.application.data.entities;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;

/**
 * Ausstattung Entity Klasse
 * Raum gehört die Beziehung, daher mapped by ausstattung in der Raum Klasse
 *
 * @author Marcel Weithoener
 */
@Entity
public class Ausstattung {

    @GeneratedValue
    @Column(nullable = false)
    @Id
    private Long id;

    @ManyToMany(mappedBy = "ausstattung")
    private Set<Raum> rooms;

    private String bez;

    public Ausstattung() {

    }

    public Ausstattung(String bez) {
        this.bez = bez;
    }

    public Set<Raum> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Raum> rooms) {
        this.rooms = rooms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBez() {
        return bez;
    }

    public void setBez(String bez) {
        this.bez = bez;
    }

    @Override
    public String toString() {
        return bez;
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
        Ausstattung that = (Ausstattung) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

package com.example.application.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import java.util.Set;

@Entity
public class Ausstattung {

    @GeneratedValue
    @Id
    private Long id;

    private String bez;
    @ManyToMany(mappedBy = "ausstattung")
    private Set<Room> rooms;

    public Ausstattung() {
        // Empty constructor is needed by Spring Data / JPA
    }

    public Ausstattung(String bez) {
        this.bez = bez;
    }

    public String getBez() {
        return bez;
    }

    public void setBez(String bez) {
        this.bez = bez;
    }
}

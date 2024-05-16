package com.example.application.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Dozent {

    @Id
    private long id;

    public Dozent() {
    }
}

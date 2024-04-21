package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.repository.AusstattungRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AusstattungService {

    private final AusstattungRepository repository;

    public AusstattungService(AusstattungRepository repository) {
        this.repository = repository;
    }

    public Set<Ausstattung> findAll() {
        return Set.copyOf(repository.findAll());
    }

}

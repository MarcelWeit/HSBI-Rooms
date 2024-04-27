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

    public void update(Ausstattung entity) {
        repository.save(entity);
    }

    public boolean existsByBez(String bez) {
        return repository.findByBez(bez) != null;
    }

    public void delete(Ausstattung entity) {
        repository.delete(entity);
    }

}

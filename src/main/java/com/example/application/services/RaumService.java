package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Raum;
import com.example.application.repository.RaumRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author marcel weithoener
 */
@Service
public class RaumService {

    private final RaumRepository repository;

    public RaumService(RaumRepository repository) {
        this.repository = repository;
    }

    public Raum save(Raum entity) {
        return repository.save(entity);
    }

    public Set<Raum> findAll() {
        return new HashSet<>(repository.findAll());
    }

    public boolean refNrExists(String refNr) {
        return repository.findByRefNr(refNr).isPresent();
    }

    public int countByAusstattungContains(Ausstattung entity) {
        return repository.countByAusstattungContains(entity);
    }

    public Set<Raum> findAllByAusstattungContains(Ausstattung entity) {
        return repository.findAllByAusstattungContains(entity);
    }

    public void delete(Raum room) {
        repository.delete(room);
    }

    public Optional<Raum> findByRefNr(String refNr) {
        return repository.findByRefNr(refNr);
    }

}

package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Raum;
import com.example.application.data.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author marcel weithoener
 */
@Service
public class RaumService {

    private final RoomRepository repository;

    public RaumService(RoomRepository repository) {
        this.repository = repository;
    }

    public void save(Raum entity) {
        repository.save(entity);
    }

    public List<Raum> findAll() {
        return repository.findAll();
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

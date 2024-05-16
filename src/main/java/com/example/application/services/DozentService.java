package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.repository.DozentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DozentService {

    private final DozentRepository repository;

    public DozentService(DozentRepository repository) {
        this.repository = repository;
    }

    public Dozent save(Dozent entity) {
        return repository.save(entity);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public List<Dozent> findAll() {
        return repository.findAll();
    }

    public void delete(Dozent dozent) {
        repository.delete(dozent);
    }

    public long count() {
        return repository.count();
    }

    public Dozent findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Dozent> findByNachname(String nachname) {
        return repository.findByNachname(nachname);
    }

    public List<Dozent> findByVornameAndNachname(String vorname, String nachname) {
        return repository.findByVornameAndNachname(vorname, nachname);
    }

    public List<Dozent> findByFachbereich(String fachbereich) {
        return repository.findByFachbereich(fachbereich);
    }
}


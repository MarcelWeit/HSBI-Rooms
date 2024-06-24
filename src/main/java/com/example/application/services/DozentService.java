package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Fachbereich;
import com.example.application.repository.DozentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Dozent> findByNachname(String nachname) {
        return repository.findByNachname(nachname);
    }
    public List<Dozent> findAllByNachname(String nachname) {
        return repository.findAllByNachname(nachname);
    }

    public Optional<Dozent> findByVornameAndNachname(String vorname, String nachname) {
        return repository.findByVornameAndNachname(vorname, nachname);
    }

    public List<Dozent> findByFachbereich(Fachbereich fachbereich) {
        return repository.findByFachbereich(fachbereich);
    }
}


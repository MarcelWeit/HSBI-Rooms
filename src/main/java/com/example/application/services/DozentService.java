package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Fachbereich;
import com.example.application.repository.DozentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service für die Entität Dozent
 *
 * @author Gabriel Greb
 */

@Service
public class DozentService {

    private final DozentRepository repository;

    public DozentService(DozentRepository repository) {
        this.repository = repository;
    }

    public List<Dozent> findAll() {
        return repository.findAll();
    }

    public Optional<Dozent> findByVornameAndNachname(String vorname, String nachname) {
        return repository.findByVornameAndNachname(vorname, nachname);
    }

    public Dozent save(Dozent entity) {
        return repository.save(entity);
    }

    public void delete(Dozent dozent) {
        repository.delete(dozent);
    }
}


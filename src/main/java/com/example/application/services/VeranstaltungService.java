package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.repository.VeranstaltungRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Service für die Entität Veranstaltung
 *
 * @author Leon Gepfner
 */
@Service
public class VeranstaltungService {

    private final VeranstaltungRepository repository;

    public VeranstaltungService(VeranstaltungRepository repository) {
        this.repository = repository;
    }

    public Set<Veranstaltung> findAll() {
        return Set.copyOf(repository.findAll());
    }

    public Set<Veranstaltung> findAllByDozent(Dozent dozent) {
        return Set.copyOf(repository.findAllByDozent(dozent));
    }
    public Optional<Veranstaltung> findById(String id) {
        return repository.findById(id);
    }

    public Veranstaltung save(Veranstaltung veranstaltung) {
        return repository.save(veranstaltung);
    }

    public void delete(Veranstaltung veranstaltung) {
        repository.delete(veranstaltung);
    }
}

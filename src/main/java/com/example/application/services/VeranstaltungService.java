package com.example.application.services;

import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.repository.VeranstaltungRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VeranstaltungService {

    private final VeranstaltungRepository repository;

    public VeranstaltungService(VeranstaltungRepository repository) {
        this.repository = repository;
    }

    public Set<Veranstaltung> findAll() {
        return Set.copyOf(repository.findAll());
    }

    public Veranstaltung findVeranstaltung(String id) {
        return repository.findById(id);
    }

    public Set<Veranstaltung> findVeranstaltungSet(String bezeichnung) {
        return Set.copyOf(repository.findAllByBezeichnung(bezeichnung));
    }

    public Set<Veranstaltung> findVeranstaltungSet(Dozent dozent) {
        return Set.copyOf(repository.findAllByDozent(dozent));
    }

    public Set<Veranstaltung> findVeranstaltungSet(Fachbereich fachbereich) {
        return Set.copyOf(repository.findAllByFachbereich(fachbereich));
    }

    public void save(Veranstaltung veranstaltung) {
        repository.save(veranstaltung);
    }

    public void delete(Veranstaltung veranstaltung) {
        repository.delete(veranstaltung);
    }
}

package com.example.application.services;

import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.repository.VeranstaltungRepository;

import java.util.Set;

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
        return Set.copyOf(repository.findByBezeichnung(bezeichnung));
    }
    /*/public Set<Veranstaltung> findVeranstaltungSet(String dozent) { Fehler weil beides String (durch Dozent Klasse ersetzen)
        return Set.copyOf(repository.findByDozent(dozent));
    }/*/
    public Set<Veranstaltung> findVeranstaltungSet(Fachbereich fachbereich) {
        return Set.copyOf(repository.findByFachbereich(fachbereich));
    }
}

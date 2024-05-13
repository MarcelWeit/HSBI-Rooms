package com.example.application.data.repository;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface VeranstaltungRepository extends JpaRepository<Veranstaltung, Long>, JpaSpecificationExecutor<Veranstaltung> {
    Veranstaltung findById(String id);
    Set<Veranstaltung> findByBezeichnung(String bezeichnung);
    Set<Veranstaltung> findByFachbereich(Fachbereich fachbereich);
    Set<Veranstaltung> findByDozent(String dozent); //Dozent mit Dozent Klasse ersetzen!!!
}

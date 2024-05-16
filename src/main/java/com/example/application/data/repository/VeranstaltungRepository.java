package com.example.application.data.repository;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface VeranstaltungRepository extends JpaRepository<Veranstaltung, Long>, JpaSpecificationExecutor<Veranstaltung> {
    Veranstaltung findById(String id);

    Set<Veranstaltung> findAllByByBezeichnung( String bezeichnung);

    Set<Veranstaltung> findAllByFachbereich( Fachbereich fachbereich);

    Set<Veranstaltung> findAllByDozent(Dozent dozent); //Dozent mit Dozent Klasse ersetzen!!!
}

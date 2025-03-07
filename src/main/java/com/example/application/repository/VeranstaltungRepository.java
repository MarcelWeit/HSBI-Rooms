package com.example.application.repository;

import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Fachbereich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository für die Entität Raum
 *
 * @author Leon Gepfner
 */
@Repository
public interface VeranstaltungRepository extends JpaRepository<Veranstaltung, String>, JpaSpecificationExecutor<Veranstaltung> {

    Optional<Veranstaltung> findById(String id);

    Set<Veranstaltung> findAllByBezeichnung(String bezeichnung);

    Set<Veranstaltung> findAllByFachbereich(Fachbereich fachbereich);

    Set<Veranstaltung> findAllByDozent(Dozent dozent);

}
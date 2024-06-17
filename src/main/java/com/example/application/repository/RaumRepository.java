package com.example.application.repository;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Raum;
import com.example.application.data.enums.Fachbereich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository für die Entität Raum
 *
 * @author Marcel Weithoener
 */
@Repository
public interface RaumRepository extends JpaRepository<Raum, String>, JpaSpecificationExecutor<Raum> {
    Set<Raum> findAllByAusstattungContains(Ausstattung entity);

    Set<Raum> findAllByFachbereich(Fachbereich entity);

    int countByAusstattungContains(Ausstattung entity);

    Optional<Raum> findByRefNr(String refNr);
}

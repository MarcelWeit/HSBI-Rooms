package com.example.application.repository;


import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Fachbereich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DozentRepository extends JpaRepository<Dozent, Long>, JpaSpecificationExecutor<Dozent> {

    List<Dozent> findAllByNachname(String nachname);

    Optional<Dozent> findByNachname(String nachname);

    Optional<Dozent> findByVornameAndNachname(String vorname, String nachname);

    List<Dozent> findByFachbereich(Fachbereich fachbereich);
}

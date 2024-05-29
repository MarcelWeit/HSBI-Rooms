package com.example.application.data.repository;


import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Fachbereich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DozentRepository extends JpaRepository<Dozent, Long>, JpaSpecificationExecutor<Dozent> {

    List<Dozent> findAllByNachname(String nachname);

    Dozent findByNachname(String nachname);

    List<Dozent> findByVornameAndNachname(String vorname, String nachname);

    List<Dozent> findByFachbereich(Fachbereich fachbereich);
}

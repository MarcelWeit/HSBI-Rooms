package com.example.application.data.repository;


import com.example.application.data.entities.Dozent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface DozentRepository extends JpaRepository<Dozent, Long>, JpaSpecificationExecutor<Dozent> {

    List<Dozent> findByNachname(String nachname);
    List<Dozent> findByVornameAndNachname(String vorname, String nachname);
    List<Dozent> findByFachbereich(String fachbereich);
}

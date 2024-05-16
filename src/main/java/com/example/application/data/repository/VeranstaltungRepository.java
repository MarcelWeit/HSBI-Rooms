package com.example.application.data.repository;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface VeranstaltungRepository extends JpaRepository<Veranstaltung, Long>, JpaSpecificationExecutor<Veranstaltung> {
//    @Query("select v from Veranstaltung as v where :id in v.id")
    Veranstaltung findById(@Param("id") String id);

//    @Query("select v from Veranstaltung as v where v.bezeichnung like %:bezeichnung%")
    Set<Veranstaltung> findByBezeichnung(@Param("bezeichnung") String bezeichnung);

//    @Query("select v from Veranstaltung as v where 'test' like %:fachbereich%")
    Set<Veranstaltung> findByFachbereich(@Param("fachbereich") Fachbereich fachbereich);

//    @Query("select v from Veranstaltung as v where v.dozent like %:dozent%")
    Set<Veranstaltung> findByDozent(@Param("dozent") String dozent); //Dozent mit Dozent Klasse ersetzen!!!
}

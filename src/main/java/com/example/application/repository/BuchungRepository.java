package com.example.application.repository;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

/**
 * Repository für Buchungen
 *
 * @author Mike Wiebe
 */
@Repository
public interface BuchungRepository extends JpaRepository<Buchung, Long>, JpaSpecificationExecutor<Buchung> {
    Buchung findBuchungById(long id);

    Set<Buchung> findAllByDozent(Dozent dozent);

    Set<Buchung> findAllByRoom(Raum room);

    Set<Buchung> findAllByVeranstaltung(Veranstaltung veranstaltung);

    Set<Buchung> findAllByDate(LocalDate date);

    Set<Buchung> findByDateAndRoom(LocalDate date, Raum room);
}

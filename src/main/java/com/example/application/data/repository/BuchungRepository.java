package com.example.application.data.repository;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Room;
import com.example.application.data.entities.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BuchungRepository extends JpaRepository<Buchung, Long>, JpaSpecificationExecutor<Buchung> {
    Buchung findBuchungById(long id);

    Set<Buchung> findAllByDozent(Dozent dozent);

    Set<Buchung> findAllByRoom(Room room);

    Set<Buchung> findAllByVeranstaltung(Veranstaltung veranstaltung);

    Set<Buchung> findAllByDate(LocalDate date);

    Set<Buchung> findByDateAndRoom(LocalDate date, Room room);
}

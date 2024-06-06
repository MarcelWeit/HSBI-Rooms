package com.example.application.repository;

import com.example.application.data.entities.*;
import com.example.application.data.enums.Zeitslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface BuchungRepository extends JpaRepository<Buchung, Long>, JpaSpecificationExecutor<Buchung> {
    Buchung findBuchungById(long id);

    Buchung findByDateAndRoomAndZeitslot(LocalDate date, Raum room, Zeitslot zeitslot);

    Set<Buchung> findAllByDozent(Dozent dozent);

    Set<Buchung> findAllByRoom(Raum room);

    Set<Buchung> findAllByVeranstaltung(Veranstaltung veranstaltung);

    Set<Buchung> findAllByDate(LocalDate date);

    Set<Buchung> findByDateAndRoom(LocalDate date, Raum room);

    Set<Buchung> findAllByUser(User user);

    Set<Buchung> findAllByUserOrDozent(User user, Dozent dozent);
}

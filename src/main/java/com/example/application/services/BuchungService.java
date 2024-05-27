package com.example.application.services;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.repository.BuchungRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class BuchungService {

    private final BuchungRepository buchungRepository;

    public BuchungService(BuchungRepository buchungRepository) {
        this.buchungRepository = buchungRepository;
    }

    public boolean roomBooked(Raum room, LocalTime startZeit, LocalTime endZeit, LocalDate date) {
        Set<Buchung> buchungenThisDay = buchungRepository.findByDateAndRoom(date, room);
        boolean belegt = false;
        for (Buchung existingBuchung : buchungenThisDay) {
            //Startet nachher und endet vor der gewünschten Zeit
            // In der gewünschten Buchung
            if (existingBuchung.getStartZeit().isAfter(startZeit) && existingBuchung.getEndZeit().isBefore(endZeit)) {
                belegt = true;
            }
            // Existierende Buchung startet nach unserer und geht noch länger als unsere
            // Aber überschneidet sich in einem Zeitraum
            if (existingBuchung.getStartZeit().isAfter(startZeit) && endZeit.isBefore(existingBuchung.getEndZeit())) {
                belegt = true;
            }
            // Startet vorher und endet während der gewünschten Zeit
            if (existingBuchung.getStartZeit().isBefore(startZeit) && existingBuchung.getEndZeit().isAfter(startZeit)) {
                belegt = true;
            }
            // Neue Buchung liegt innerhalb einer existierenden Buchung
            if (startZeit.isAfter(existingBuchung.getStartZeit()) && startZeit.isBefore(existingBuchung.getEndZeit())) {
                belegt = true;
            }
            // Neue Buchung fängt vor einer existierenden an und hört nach einer existierenden auf
            if (startZeit.isBefore(existingBuchung.getEndZeit()) && endZeit.isAfter(existingBuchung.getEndZeit())) {
                belegt = true;
            }
            // Startet zur gleichen Zeit egal wann die Veranstaltung endet
            if (existingBuchung.getStartZeit().equals(startZeit)) {
                belegt = true;
            }
        }
        return belegt;
    }

    public Buchung save(Buchung buchung) {
        return buchungRepository.save(buchung);
    }

    public boolean existsById(Long id) {
        return buchungRepository.existsById(id);
    }

    public List<Buchung> findAll() {
        return buchungRepository.findAll();
    }

    public void delete(Buchung buchung) {
        buchungRepository.delete(buchung);
    }

    public long countAll() {
        return buchungRepository.count();
    }

    public Buchung findById(Long id) {
        return buchungRepository.findBuchungById(id);
    }

    public Set<Buchung> findAllByDozent(Dozent dozent) {
        return Set.copyOf(buchungRepository.findAllByDozent(dozent));
    }

    public Set<Buchung> findAllByRoom(Raum room) {
        return Set.copyOf(buchungRepository.findAllByRoom(room));
    }

    public Set<Buchung> findAllByVeranstaltung(Veranstaltung veranstaltung) {
        return Set.copyOf(buchungRepository.findAllByVeranstaltung(veranstaltung));
    }

    public Set<Buchung> findAllByDate(LocalDate date) {
        return Set.copyOf(buchungRepository.findAllByDate(date));
    }

    public Set<Buchung> findAllbyDateAndRoom(LocalDate date, Raum room) {
        return Set.copyOf(buchungRepository.findByDateAndRoom(date, room));
    }
}

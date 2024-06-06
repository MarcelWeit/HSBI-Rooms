package com.example.application.services;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Zeitslot;
import com.example.application.repository.BuchungRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BuchungService {

    private final BuchungRepository buchungRepository;

    public BuchungService(BuchungRepository buchungRepository) {
        this.buchungRepository = buchungRepository;
    }

    public boolean roomBooked(Raum room, Zeitslot zeitslot, LocalDate date) {
        return buchungRepository.findByDateAndRoomAndZeitslot(date, room, zeitslot).isPresent();
    }

    public Buchung save(Buchung buchung) {
        return buchungRepository.save(buchung);
    }

    public Optional<Buchung> findByDateAndRoomAndZeitslot(LocalDate date, Raum room, Zeitslot zeitslot) {
        return buchungRepository.findByDateAndRoomAndZeitslot(date, room, zeitslot);
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

    public Optional<Buchung> findById(Long id) {
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

    public Set<Buchung> findAllByDateAndRoom(LocalDate date, Raum room) {
        return Set.copyOf(buchungRepository.findByDateAndRoom(date, room));
    }
}

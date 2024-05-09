package com.example.application.services;

import com.example.application.data.entities.Veranstaltung;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VeranstaltungService{
    public void delete(Veranstaltung veranstaltung) {

    }

    public List<Veranstaltung> findAll() {
        List<Veranstaltung> veranstaltungen = new ArrayList<>();
        Veranstaltung exampleVeranstaltung = new Veranstaltung();
        veranstaltungen.add(exampleVeranstaltung);
        return veranstaltungen;
    }

    public Veranstaltung findById(long id) {
        return null;
    }

    public void save(Veranstaltung veranstaltung) {
    }
}

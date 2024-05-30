package com.example.application.services;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Raum;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Raumtyp;
import com.example.application.repository.RaumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testet alle Methoden der RaumService Klasse
 *
 * @author marcel weithoener
 */
@ExtendWith(MockitoExtension.class)
public class RaumServiceTest {

    @Mock
    private RaumRepository raumRepository;

    @InjectMocks
    private RaumService raumService;

    @Mock
    private AusstattungService ausstattungService;

    @Test
    void save() {
        Raum raumToTest = new Raum("C1", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        when(raumRepository.save(raumToTest)).thenReturn(raumToTest);

        Raum savedRaum = raumService.save(raumToTest);

        assertThat(savedRaum).isNotNull();
        assertThat(savedRaum.getRefNr()).isEqualTo(raumToTest.getRefNr());
    }

    @Test
    void findAll() {
        List<Raum> raumListe = new ArrayList<>();
        raumListe.add(new Raum("C2", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1"));
        raumListe.add(new Raum("C3", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1"));
        when(raumRepository.findAll()).thenReturn(raumListe);

        raumService.save(raumListe.get(0));
        raumService.save(raumListe.get(1));
        List<Raum> returnedList = new ArrayList<>(raumService.findAll());

        assertThat(returnedList.size()).isEqualTo(2);
        assertThat(returnedList).isNotNull();
        assertThat(returnedList).isEqualTo(raumListe);
    }

    @Test
    void refNrExists() {
        Raum r = new Raum("C4", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        when(raumRepository.findByRefNr("C4")).thenReturn(java.util.Optional.of(r));
        raumService.save(r);
        assertThat(raumService.refNrExists("C4")).isTrue();
    }

    @Test
    void countByAusstattungContains() {
        Raum r = new Raum("C5", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        Ausstattung a = new Ausstattung("Beamer");
        r.addAusstattung(a);
        when(raumRepository.countByAusstattungContains(a)).thenReturn(1);
        raumService.save(r);
        assertThat(raumService.countByAusstattungContains(a)).isEqualTo(1);
    }

    @Test
    void findAllByAusstattungContains() {
        Raum r = new Raum("C6", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        Ausstattung a = new Ausstattung("Beamer");
        r.addAusstattung(a);
        when(raumRepository.findAllByAusstattungContains(a)).thenReturn(Set.of(r));
        raumService.save(r);
        assertThat(raumService.findAllByAusstattungContains(a)).containsExactlyInAnyOrder(r);
        assertThat(raumService.findAllByAusstattungContains(a)).isNotNull();
    }

    @Test
    void delete() {
        Raum r = new Raum("C7", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        raumService.save(r);
        raumService.delete(r);
        assertThat(raumService.refNrExists("C7")).isFalse();
    }

    @Test
    void findByRefNr() {
        Raum r = new Raum("C8", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        when(raumRepository.findByRefNr("C8")).thenReturn(java.util.Optional.of(r));
        raumService.save(r);
        assert raumService.findByRefNr("C8").isPresent();
    }

}
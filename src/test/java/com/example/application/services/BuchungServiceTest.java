package com.example.application.services;

import com.example.application.data.entities.*;
import com.example.application.data.enums.*;
import com.example.application.repository.BuchungRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Testet alle Methoden der BuchungService Klasse
 *
 * @author Mike Wiebe
 */
@ExtendWith(MockitoExtension.class)
public class BuchungServiceTest {

    @Mock
    BuchungRepository buchungRepository;

    @InjectMocks
    BuchungService buchungService;

    @Test
    void save() {
        Raum raum = new Raum("F1", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        Dozent dozent = new Dozent(Anrede.HERR, "Test1", "Test1", Fachbereich.WIRTSCHAFT, "");
        Veranstaltung veranstaltung = new Veranstaltung("CFR1", "Test1", dozent, 50, Fachbereich.WIRTSCHAFT);
        Buchung buchung = new Buchung();
        buchung.setRoom(raum);
        buchung.setDozent(dozent);
        buchung.setVeranstaltung(veranstaltung);
        buchung.setDate(LocalDate.of(2000, 1, 1));
        buchung.setZeitslot(Zeitslot.EINS);

        when(buchungRepository.save(buchung)).thenReturn(buchung);
        Buchung buchungSaved = buchungService.save(buchung);

        assertThat(buchungSaved).isNotNull();
        assertThat(buchungSaved.getRoom()).isEqualTo(raum);
        assertThat(buchungSaved.getDozent()).isEqualTo(dozent);
        assertThat(buchungSaved.getVeranstaltung()).isEqualTo(veranstaltung);
    }

    @Test
    void findByDateAndRoomAndZeitslot() {
        Buchung buchung = createBuchung("2");
        Raum raum = new Raum("F2", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        
        when(buchungRepository.findByDateAndRoomAndZeitslot(LocalDate.of(2000, 1, 2), raum, Zeitslot.EINS)).thenReturn(java.util.Optional.of(buchung));
        buchungService.save(buchung);
        assert buchungService.findByDateAndRoomAndZeitslot(LocalDate.of(2000, 1, 2), raum, Zeitslot.EINS).isPresent();
    }

    @Test
    void findAll() {
        List<Buchung> buchungList = new ArrayList<>();

        Buchung buchung1 = createBuchung("3");
        Buchung buchung2 = createBuchung("4");
        
        buchungList.add(buchung1);
        buchungList.add(buchung2);
        when(buchungRepository.findAll()).thenReturn(buchungList);
        
        buchungService.save(buchungList.get(0));
        buchungService.save(buchungList.get(1));
        
        List<Buchung> returnedBuchungList = new ArrayList<>(buchungService.findAll());
        assertThat(returnedBuchungList.size()).isEqualTo(2);
        assertThat(returnedBuchungList.get(0)).isEqualTo(buchung1);
        assertThat(returnedBuchungList.get(1)).isEqualTo(buchung2);
        assertThat(returnedBuchungList).isNotNull();
        assertThat(returnedBuchungList).isEqualTo(buchungList);
    }
    
    @Test
    void delete() {
        Buchung buchung = createBuchung("5");
        Raum raum = new Raum("F5", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        
        buchungService.save(buchung);
        buchungService.delete(buchung);
        assertThat(buchungService.findByDateAndRoomAndZeitslot(LocalDate.of(2000, 1, 5), raum, Zeitslot.EINS)).isEmpty();
    }
    
    @Test
    void findAllByDozent() {
        Buchung buchung = createBuchung("6");
        Dozent dozent = new Dozent(Anrede.HERR, "Test5", "Test5", Fachbereich.WIRTSCHAFT, "");
        when(buchungRepository.findAllByDozent(dozent)).thenReturn(Set.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.findAllByDozent(dozent)).isNotNull();
        assertThat(buchungService.findAllByDozent(dozent)).containsExactly(buchung);
    }

    @Test
    void findAllByUser() {
        Buchung buchung = createBuchung("7");
        User user = new User("Test7@gmail.com", "Test7", "Test7", " ", Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT, Anrede.HERR, "");
        when(buchungRepository.findAllByUser(user)).thenReturn(Set.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.findAllByUser(user)).isNotNull();
        assertThat(buchungService.findAllByUser(user)).containsExactly(buchung);
    }

    @Test
    void findAllByRoom() {
        Buchung buchung = createBuchung("8");
        Raum raum = new Raum("F8", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        when(buchungRepository.findAllByRoom(raum)).thenReturn(Set.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.findAllByRoom(raum)).isNotNull();
        assertThat(buchungService.findAllByRoom(raum)).containsExactly(buchung);
    }

    @Test
    void findAllByVeranstaltung() {
        Buchung buchung = createBuchung("9");
        Dozent dozent = new Dozent(Anrede.HERR, "Test9", "Test9", Fachbereich.WIRTSCHAFT, "");
        Veranstaltung veranstaltung = new Veranstaltung("CFR9", "Test9", dozent, 50, Fachbereich.WIRTSCHAFT);
        when(buchungRepository.findAllByVeranstaltung(veranstaltung)).thenReturn(Set.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.findAllByVeranstaltung(veranstaltung)).isNotNull();
        assertThat(buchungService.findAllByVeranstaltung(veranstaltung)).containsExactly(buchung);
    }

    @Test
    void findAllByDateAndRoom() {
        Buchung buchung = createBuchung("10");
        Raum raum = new Raum("F10", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        when(buchungRepository.findByDateAndRoom(LocalDate.of(2000, 1, 10), raum)).thenReturn(Set.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.findAllByDateAndRoom(LocalDate.of(2000, 1, 10), raum)).isNotNull();
        assertThat(buchungService.findAllByDateAndRoom(LocalDate.of(2000, 1, 10), raum)).containsExactly(buchung);
    }

    @Test
    void findAllByUserOrDozent() {
        Buchung buchung = createBuchung("11");
        User user = new User("Test11@gmail.com", "Test11", "Test11", " ", Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT, Anrede.HERR, "");
        Dozent dozent = new Dozent(Anrede.HERR, "Test11", "Test11", Fachbereich.WIRTSCHAFT, "");
        when(buchungRepository.findAllByUserOrDozent(user, dozent)).thenReturn(Set.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.findAllByUserOrDozent(user, dozent)).isNotNull();
        assertThat(buchungService.findAllByUserOrDozent(user, dozent)).containsExactly(buchung);
    }

    @Test
    void roomBooked() {
        Buchung buchung = createBuchung("12");
        Raum raum = new Raum("F12", Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        when(buchungRepository.findByDateAndRoomAndZeitslot(LocalDate.of(2000, 1, 12), raum, Zeitslot.EINS)).thenReturn(java.util.Optional.of(buchung));
        buchungService.save(buchung);
        assertThat(buchungService.roomBooked(raum, Zeitslot.EINS, LocalDate.of(2000, 1, 12))).isTrue();
        assertThat(buchungService.roomBooked(raum, Zeitslot.ZWEI, LocalDate.of(2000, 1, 12))).isFalse();
    }

    private static Buchung createBuchung(String number) {
        Raum raum = new Raum("F".concat(number), Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "FB Wirtschaft Etage 1");
        Dozent dozent = new Dozent(Anrede.HERR, "Test".concat(number), "Test".concat(number), Fachbereich.WIRTSCHAFT, "");
        Veranstaltung veranstaltung = new Veranstaltung("CFR".concat(number), "Test".concat(number), dozent, 50, Fachbereich.WIRTSCHAFT);
        User user = new User("Test".concat(number).concat("@gmail.com"), "Test".concat(number), "Test".concat(number), " ", Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT, Anrede.HERR, "");
        Buchung buchung = new Buchung();
        buchung.setRoom(raum);
        buchung.setDozent(dozent);
        buchung.setVeranstaltung(veranstaltung);
        buchung.setUser(user);
        buchung.setDate(LocalDate.of(2000, 1, Integer.parseInt(number)));
        buchung.setZeitslot(Zeitslot.EINS);
        return buchung;
    }

}

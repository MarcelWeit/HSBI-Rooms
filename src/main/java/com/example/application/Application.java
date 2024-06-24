package com.example.application;

import com.example.application.data.entities.*;
import com.example.application.data.enums.*;
import com.example.application.repository.*;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @Author Marcel Weithoener
 *
 * Wird bei dem Start der Anwendung ausgeführt und initialisiert die Datenbank mit Testdaten.
 */
@SpringBootApplication
@Theme(value = "raumbuchung")
public class Application implements AppShellConfigurator, CommandLineRunner {

    private final AusstattungRepository ausstattungRepository;
    private final RaumRepository roomRepository;
    private final UserRepository userRepository;
    private final VeranstaltungRepository veranstaltungRepository;
    private final DozentRepository dozentRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationRepository registrationRepository;
    private final BuchungRepository buchungRepository;

    public Application(AusstattungRepository ausstattungRepository, RaumRepository roomRepository, UserRepository userRepository,
                       VeranstaltungRepository veranstaltungRepository, DozentRepository dozentRepository, PasswordEncoder passwordEncoder,
                       RegistrationRepository registrationRepository, BuchungRepository buchungRepository) {
        this.ausstattungRepository = ausstattungRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.veranstaltungRepository = veranstaltungRepository;
        this.dozentRepository = dozentRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrationRepository = registrationRepository;
        this.buchungRepository = buchungRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        if (ausstattungRepository.count() == 0) {
            ausstattungRepository.save(new Ausstattung("Beamer"));
            ausstattungRepository.save(new Ausstattung("Pult"));
            ausstattungRepository.save(new Ausstattung("Whiteboard"));
            ausstattungRepository.save(new Ausstattung("Mikrofon"));
            ausstattungRepository.save(new Ausstattung("Soundanlage"));
            ausstattungRepository.save(new Ausstattung("Kamera"));
        }
        if (roomRepository.count() == 0) {
            for (int i = 1; i < 5; i++) {
                Raum room = new Raum("C" + i, Raumtyp.HOERSAAL, 100, Fachbereich.WIRTSCHAFT, "Fachbereich Wirtschaft Etage 1");
                room.addAusstattung(ausstattungRepository.findByBez("Beamer").orElse(null));
                room.addAusstattung(ausstattungRepository.findByBez("Whiteboard").orElse(null));
                room.addAusstattung(ausstattungRepository.findByBez("Kamera").orElse(null));
                roomRepository.save(room);
            }
            for (int b = 1; b < 5; b++) {
                Raum room = new Raum("A" + b, Raumtyp.HOERSAAL, 100, Fachbereich.SOZIALWESEN, "Fachbereich Sozialwesen Etage 1");
                room.addAusstattung(ausstattungRepository.findByBez("Pult").orElse(null));
                room.addAusstattung(ausstattungRepository.findByBez("Soundanlage").orElse(null));
                roomRepository.save(room);
            }
            for (int c = 1; c < 5; c++) {
                Raum room = new Raum("A" + c, Raumtyp.HOERSAAL, 100, Fachbereich.GESTALTUNG, "Fachbereich Gestaltung Etage 1");
                room.addAusstattung(ausstattungRepository.findByBez("Pult").orElse(null));
                room.addAusstattung(ausstattungRepository.findByBez("Kamera").orElse(null));
                roomRepository.save(room);
            }
            roomRepository.save(new Raum("C331", Raumtyp.SEMINARRAUM, 60, Fachbereich.WIRTSCHAFT, "Fachbereich Wirtschaft Etage 3"));
        }
        if (userRepository.count() == 0) {
            User admin = new User("admin@gmail.com", "Mustermann", "Max", passwordEncoder.encode("admin"), Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT, Anrede.HERR,
                    "Prof. Dr.");
            userRepository.save(admin);
            User dozent = new User("jkuester@hsbi.de", "Küster", "Jochen", passwordEncoder.encode("kuester"), Set.of(Role.DOZENT), Fachbereich.WIRTSCHAFT, Anrede.HERR,
                    "Prof. Dr.");
            userRepository.save(dozent);
            User dozentinSoziales = new User("smeier@hsbi.de", "Meier", "Sabine", passwordEncoder.encode("sozial"), Set.of(Role.DOZENT), Fachbereich.SOZIALWESEN,
                    Anrede.FRAU,
                    "Prof. Dr. rer. nat.");
            userRepository.save(dozentinSoziales);
            User fbplan = new User("fbplanung@gmail.com", "Mustermann", "Max", passwordEncoder.encode("fbplanung"), Set.of(Role.FBPLANUNG), Fachbereich.SOZIALWESEN, Anrede.HERR,
                    "Prof. Dr.");
            userRepository.save(fbplan);
        }
        if (dozentRepository.count() == 0) {
            dozentRepository.save(new Dozent(Anrede.HERR, "Wiemann", "Volker", Fachbereich.WIRTSCHAFT, "Prof. Dr. rer. pol., Dipl.-Kfm."));
            dozentRepository.save(new Dozent(Anrede.HERR, "Küster", "Jochen", Fachbereich.WIRTSCHAFT, "Prof. Dr. rer. nat."));
            dozentRepository.save(new Dozent(Anrede.HERR, "Hartel", "Peter", Fachbereich.WIRTSCHAFT, "Prof. Dr.-Ing., Dipl.-Inform."));
            dozentRepository.save(new Dozent(Anrede.HERR, "Schaefermeier", "Ulrich ", Fachbereich.WIRTSCHAFT, "Prof. Dr. rer. pol."));
            dozentRepository.save(new Dozent(Anrede.FRAU, "Rau", "Meiken", Fachbereich.GESTALTUNG, "Prof. Dipl.-Des."));
            dozentRepository.save(new Dozent(Anrede.FRAU, "Meier", "Sabine", Fachbereich.SOZIALWESEN, "Prof. Dr. rer. nat."));
            dozentRepository.save(new Dozent(Anrede.FRAU, "Peters", "Ulrike", Fachbereich.GESUNDHEIT, "Dr. rer. pol."));
            dozentRepository.save(new Dozent(Anrede.FRAU, "Engelnicht", "Anke", Fachbereich.INGENIEURWISSENSCHAFTENUNDMATHEMATIK, "Prof. Dr.-Ing."));

        }
        if (veranstaltungRepository.count() == 0) {
            dozentRepository.findByNachname("Küster").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("CFR23", "Software Engineering", dozent, 100, Fachbereich.WIRTSCHAFT)));
            dozentRepository.findByNachname("Wiemann").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("CGRH26", "Internes Rechnungswesen", dozent, 120, Fachbereich.WIRTSCHAFT)));
            dozentRepository.findByNachname("Hartel").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("CGRH27", "Datenbanken", dozent, 120,
                    Fachbereich.WIRTSCHAFT)));
            dozentRepository.findByNachname("Schäfermeier").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("CGRH28", "Wirtschaftsinformatik",
                    dozent, 120, Fachbereich.WIRTSCHAFT)));
            dozentRepository.findByNachname("Rau").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("AGRH29", "Grafikdesign", dozent, 120, Fachbereich.GESTALTUNG)));
            dozentRepository.findByNachname("Meier").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("AGRH30", "Sozialpsychologie", dozent, 120, Fachbereich.SOZIALWESEN)));
            dozentRepository.findByNachname("Peters").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("AGRH31", "Gesundheitsökonomie", dozent, 120, Fachbereich.GESUNDHEIT)));
            dozentRepository.findByNachname("Engelnicht").ifPresent(dozent -> veranstaltungRepository.save(new Veranstaltung("AGRH32", "Mathematik", dozent, 120, Fachbereich.INGENIEURWISSENSCHAFTENUNDMATHEMATIK)));
        }
        if (registrationRepository.count() == 0) {
            Registrierung r = new Registrierung("register@gmail.com", "Meyer", "Sabine", "", Role.DOZENT, Fachbereich.WIRTSCHAFT, Anrede.FRAU, "Prof. Dr.");
            r.setHashedPassword(passwordEncoder.encode("register"));
            registrationRepository.save(r);
        }
        if (buchungRepository.count() == 0) {
            Optional<Raum> raum = roomRepository.findByRefNr("C1");
            List<Veranstaltung> veranstaltungen = veranstaltungRepository.findAll();
            Optional<Dozent> dozent = dozentRepository.findByNachname("Küster");
            if (raum.isPresent() && dozent.isPresent()) {
                LocalDate date = LocalDate.of(2024, 5, 1);
                while (date.isBefore(LocalDate.of(2024, 6, 1))) {
                    int count = 0;
                    for (Zeitslot zeitslot : Zeitslot.values()) {
                        buchungRepository.save(new Buchung(date, zeitslot, raum.get(), veranstaltungen.get(count), dozent.get()));
                        count++;
                    }
                    date = date.plusDays(1);
                }
            }
        }
        System.out.println("Datenbank initialisiert");
    }
}

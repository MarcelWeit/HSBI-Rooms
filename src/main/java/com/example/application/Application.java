package com.example.application;

import com.example.application.data.entities.*;
import com.example.application.data.enums.Anrede;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Raumtyp;
import com.example.application.data.enums.Role;
import com.example.application.repository.*;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * The entry point of the Spring Boot application.
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
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

    public Application(AusstattungRepository ausstattungRepository, RaumRepository roomRepository, UserRepository userRepository, VeranstaltungRepository veranstaltungRepository, DozentRepository dozentRepository, PasswordEncoder passwordEncoder, RegistrationRepository registrationRepository) {
        this.ausstattungRepository = ausstattungRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.veranstaltungRepository = veranstaltungRepository;
        this.dozentRepository = dozentRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrationRepository = registrationRepository;
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
            System.out.println("Ausstattung Daten initialisiert");
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
            roomRepository.save(new Raum("C331", Raumtyp.SEMINARRAUM, 60, Fachbereich.WIRTSCHAFT, "Fachbereich Wirtschaft Etage 3"));
            System.out.println("Raum Daten initialisiert");
        }
        if (userRepository.count() == 0) {
            User admin = new User("admin@gmail.com", "Mustermann", "Max", passwordEncoder.encode("admin"), Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT, Anrede.HERR,
                    "Prof. Dr.");
            userRepository.save(admin);
            User dozent = new User("jkuester@hsbi.de", "Küster", "Jochen", passwordEncoder.encode("kuester"), Set.of(Role.DOZENT), Fachbereich.WIRTSCHAFT, Anrede.HERR,
                    "Prof. Dr.");
            userRepository.save(dozent);
            User fbplan = new User("fbplanung@gmail.com", "Mustermann", "Max", passwordEncoder.encode("fbplanung"), Set.of(Role.FBPLANUNG), Fachbereich.SOZIALWESEN, Anrede.HERR,
                    "Prof. Dr.");
            userRepository.save(fbplan);
            System.out.println("User Daten initialisiert");
        }
        if (dozentRepository.count() == 0) {
            dozentRepository.save(new Dozent("Wiemann", "Volker", Fachbereich.WIRTSCHAFT));
            dozentRepository.save(new Dozent("Küster", "Jochen", Fachbereich.WIRTSCHAFT));
            dozentRepository.save(new Dozent("Hartel", "Peter", Fachbereich.WIRTSCHAFT));
            System.out.println("Dozent Daten initialisiert");
        }
        if (veranstaltungRepository.count() == 0) {
            veranstaltungRepository.save(new Veranstaltung("CFR23", "Software Engineering", dozentRepository.findByNachname("Küster").get(), 100, Fachbereich.WIRTSCHAFT));
            veranstaltungRepository.save(new Veranstaltung("CGRH26", "Internes Rechnungswesen", dozentRepository.findByNachname("Wiemann").get(), 120, Fachbereich.WIRTSCHAFT));
            System.out.println("Veranstaltung Daten initialisiert");
        }
        if (registrationRepository.count() == 0) {
            Registrierung r = new Registrierung("register@gmail.com", "Meyer", "Sabine", "", Role.DOZENT, Fachbereich.WIRTSCHAFT, Anrede.FRAU, "Prof. Dr.");
            r.setHashedPassword(passwordEncoder.encode("register"));
            registrationRepository.save(r);
            System.out.println("Registrierung Daten initialisiert");
        }
    }
}

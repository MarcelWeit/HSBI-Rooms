package com.example.application;

import com.example.application.data.entities.*;
import com.example.application.data.repository.*;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AusstattungRepository ausstattungRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VeranstaltungRepository veranstaltungRepository;

    @Autowired
    private DozentRepository dozentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                room.addAusstattung(ausstattungRepository.findByBez("Beamer"));
                room.addAusstattung(ausstattungRepository.findByBez("Whiteboard"));
                room.addAusstattung(ausstattungRepository.findByBez("Kamera"));
                roomRepository.save(room);
            }
            for (int b = 1; b < 5; b++) {
                Raum room = new Raum("A" + b, Raumtyp.HOERSAAL, 100, Fachbereich.SOZIALWESEN, "Fachbereich Sozialwesen Etage 1");
                room.addAusstattung(ausstattungRepository.findByBez("Pult"));
                room.addAusstattung(ausstattungRepository.findByBez("Soundanlage"));
                room.addAusstattung(ausstattungRepository.findByBez("test"));
                roomRepository.save(room);
            }
            roomRepository.save(new Raum("C331", Raumtyp.SEMINARRAUM, 60, Fachbereich.WIRTSCHAFT, "Fachbereich Wirtschaft Etage 3"));
        }
        if (userRepository.findByUsername("admin@gmail.com") == null) {
            User user = new User("admin@gmail.com", "Mustermann", "Max", "", Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT);
            user.setHashedPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
        if (dozentRepository.count() == 0) {
            dozentRepository.save(new Dozent("Wiemann", "Volker", Fachbereich.WIRTSCHAFT));
            dozentRepository.save(new Dozent("Küster", "Jochen", Fachbereich.WIRTSCHAFT));
            dozentRepository.save(new Dozent("Hartel", "Peter", Fachbereich.WIRTSCHAFT));
        }
        if (veranstaltungRepository.count() == 0) {
            veranstaltungRepository.save(new Veranstaltung("CFR23", "SoftwareEngineering", dozentRepository.findByNachname("Küster"), 100, Fachbereich.WIRTSCHAFT));
            veranstaltungRepository.save(new Veranstaltung("CGRH26", "InternesRechnungswesen", dozentRepository.findByNachname("Wiemann"), 120, Fachbereich.WIRTSCHAFT));
        }
    }
}

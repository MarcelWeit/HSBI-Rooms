package com.example.application;

import com.example.application.data.entities.*;
import com.example.application.data.repository.AusstattungRepository;
import com.example.application.data.repository.RoomRepository;
import com.example.application.data.repository.UserRepository;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Set;

/**
 * The entry point of the Spring Boot application.
 * <p>
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
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
                                                                               SqlInitializationProperties properties, UserRepository userRepository) {
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {

                return false;
            }
        };
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists (optional)
        if (ausstattungRepository.count() == 0) {
            ausstattungRepository.save(new Ausstattung("Beamer"));
            ausstattungRepository.save(new Ausstattung("Computer"));
            ausstattungRepository.save(new Ausstattung("Whiteboard"));
            ausstattungRepository.save(new Ausstattung("Overheadprojektor"));
            ausstattungRepository.save(new Ausstattung("Leinwand"));
            ausstattungRepository.save(new Ausstattung("Laptop"));
            ausstattungRepository.save(new Ausstattung("Mikrofon"));
            ausstattungRepository.save(new Ausstattung("Lautsprecher"));
            ausstattungRepository.save(new Ausstattung("Kamera"));
            ausstattungRepository.save(new Ausstattung("Drucker"));
            ausstattungRepository.save(new Ausstattung("Scanner"));
            ausstattungRepository.save(new Ausstattung("Smartboard"));
            ausstattungRepository.save(new Ausstattung("Tablet"));
            ausstattungRepository.save(new Ausstattung("Telefon"));
        }
        if (roomRepository.count() == 0) {
            for (int i = 0; i < 20; i++) {
                Room room = new Room("A" + i, Raumtyp.HOERSAAL, 100, Fachbereich.INGENIEURWISSENSCHAFTENUNDMATHEMATIK, "FB IuM EG");
                room.addAusstattung(ausstattungRepository.findByBez("Beamer"));
                room.addAusstattung(ausstattungRepository.findByBez("Whiteboard"));
                room.addAusstattung(ausstattungRepository.findByBez("Computer"));
                roomRepository.save(room);
            }
        }
        if (userRepository.count() == 0) {
            User user = new User("max@gmail.com", "max", "mustermann", "", Set.of(Role.ADMIN), Fachbereich.WIRTSCHAFT);
            user.setHashedPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
    }
}

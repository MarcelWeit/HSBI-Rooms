package com.example.application.views;

import com.example.application.data.entities.Registrierung;
import com.example.application.data.entities.User;
import com.example.application.services.RegistrationService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Set;

@Route(value = "freischalten", layout = MainLayout.class)
@PageTitle("User freischalten")
@RolesAllowed("ADMIN")
public class FreischaltenView extends VerticalLayout {

    private final Grid<Registrierung> grid;
    private final RegistrationService registrationService;
    private final UserService userService;

    public FreischaltenView(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.grid = new Grid<>(Registrierung.class, false);
        setupGrid();
        grid.setItems(registrationService.findAllRegistrierungen());
        add(grid);
    }
    // Methode zur Einrichtung des Grids
    private void setupGrid() {
        grid.setColumns("lastName", "firstName", "fachbereich", "role", "username");

        grid.getColumnByKey("username").setHeader("Benutzername");
        grid.getColumnByKey("firstName").setHeader("Vorname");
        grid.getColumnByKey("lastName").setHeader("Nachname");
        grid.getColumnByKey("role").setHeader("Rolle");
        grid.getColumnByKey("fachbereich").setHeader("Fachbereich");

        grid.addComponentColumn(registrierung -> {
            HorizontalLayout buttonsLayout = new HorizontalLayout();

            // Freischalten Button
            Button approveButton = new Button("Freischalten");
            approveButton.addClickListener(event -> approveRegistration(registrierung));
            buttonsLayout.add(approveButton);

            // Ablehnen Button
            Button deleteButton = new Button("Ablehnen");
            deleteButton.addClickListener(event -> deleteRegistration(registrierung));
            buttonsLayout.add(deleteButton);

            return buttonsLayout;
        }).setHeader("Aktionen");
    }

    // Methode zur Freischaltung der Registrierung
    public void approveRegistration(Registrierung registrierung) {
        User user = new User();
        user.setUsername(registrierung.getUsername());
        user.setFirstName(registrierung.getFirstName());
        user.setLastName(registrierung.getLastName());
        user.setHashedPassword(registrierung.getHashedPassword());
        user.setRoles(Set.of(registrierung.getRole()));
        user.setFachbereich(registrierung.getFachbereich());
        user.setAnrede((registrierung.getAnrede()));
        user.setAkadTitel((registrierung.getAkadTitel()));

        userService.save(user);
        registrationService.delete(registrierung);
        grid.setItems(registrationService.findAllRegistrierungen());
        //Notification
        Notification.show("Registrierung freigeschaltet", 3000, Notification.Position.MIDDLE);
    }
    // Methode zum Löschen der Registrierung
    private void deleteRegistration(Registrierung registrierung) {
        registrationService.delete(registrierung);
        grid.setItems(registrationService.findAllRegistrierungen());
        Notification.show("Registrierung abgelehnt", 3000, Notification.Position.MIDDLE);
    }

}



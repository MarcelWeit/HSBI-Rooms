package com.example.application.views;

import com.example.application.data.entities.Registrierung;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "freischalten", layout = MainLayout.class)
@PageTitle("User freischalten")
@RolesAllowed("ADMIN")
public class FreischaltenView extends VerticalLayout {

    private final Grid<Registrierung> grid;
    private final UserService userService;

    public FreischaltenView(UserService userService) {
        this.userService = userService;
        this.grid = new Grid<>(Registrierung.class, false);
        setupGrid();
        add(grid);
    }

    private void setupGrid() {
        grid.setColumns("firstName", "lastName", "fachbereich", "role", "username");

        grid.getColumnByKey("username").setHeader("Benutzername");
        grid.getColumnByKey("firstName").setHeader("Vorname");
        grid.getColumnByKey("lastName").setHeader("Nachname");
        grid.getColumnByKey("role").setHeader("Rolle");
        grid.getColumnByKey("fachbereich").setHeader("Fachbereich");

        grid.addComponentColumn(registrierung -> {
            Button approveButton = new Button("Freischalten");
            approveButton.addClickListener(event -> approveRegistration(registrierung));
            return approveButton;
        }).setHeader("Freischalten");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.setItems(userService.findAllRegistrierungen());
    }

    private void approveRegistration(Registrierung registrierung) {
        userService.approveRegistration(registrierung);
        grid.setItems(userService.findAllRegistrierungen());
    }
}



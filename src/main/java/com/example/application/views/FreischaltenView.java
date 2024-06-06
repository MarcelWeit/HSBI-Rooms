package com.example.application.views;

import com.example.application.data.entities.Registrierung;
import com.example.application.services.FreischaltenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "freischalten", layout = MainLayout.class)
@PageTitle("User freischalten")
@RolesAllowed("ADMIN")
public class FreischaltenView extends VerticalLayout {

    private final Grid<Registrierung> grid;
    private final FreischaltenService freischaltenService;

    public FreischaltenView(FreischaltenService freischaltenService) {
        this.freischaltenService = freischaltenService;
        this.grid = new Grid<>(Registrierung.class, false);
        setupGrid();
        grid.setItems(freischaltenService.findAllRegistrierungen());
        add(grid);
    }

    private void setupGrid() {
        grid.setColumns("lastName", "firstName", "fachbereich", "role", "username");

        grid.getColumnByKey("username").setHeader("Benutzername");
        grid.getColumnByKey("firstName").setHeader("Vorname");
        grid.getColumnByKey("lastName").setHeader("Nachname");
        grid.getColumnByKey("role").setHeader("Rolle");
        grid.getColumnByKey("fachbereich").setHeader("Fachbereich");

        grid.addComponentColumn(registrierung -> {
            HorizontalLayout buttonsLayout = new HorizontalLayout();

            Button approveButton = new Button("Freischalten");
            approveButton.addClickListener(event -> approveRegistration(registrierung));
            buttonsLayout.add(approveButton);

            Button deleteButton = new Button("Ablehnen");
            deleteButton.addClickListener(event -> deleteRegistration(registrierung));
            buttonsLayout.add(deleteButton);

            return buttonsLayout;
        }).setHeader("Aktionen");
    }

    private void approveRegistration(Registrierung registrierung) {
        freischaltenService.approveRegistration(registrierung);
        grid.setItems(freischaltenService.findAllRegistrierungen());
        //Notification
        Notification.show("Registrierung freigeschaltet", 3000, Notification.Position.MIDDLE);
    }
    private void deleteRegistration(Registrierung registrierung) {
        freischaltenService.delete(registrierung);
        grid.setItems(freischaltenService.findAllRegistrierungen());
        Notification.show("Registrierung abgelehnt", 3000, Notification.Position.MIDDLE);
    }
}



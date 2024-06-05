package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Set;


@Route(value = "meine-buchungen", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
@PageTitle("MeineBuchungen")
public class MeineBuchungenView extends VerticalLayout {

    private final BuchungService buchungService;
    private final UserService userService;
    private final DozentService dozentService;

    private final AuthenticatedUser currentUser;

    private Grid<Buchung> grid;

    public MeineBuchungenView(BuchungService buchungService, UserService userService, DozentService dozentService, AuthenticatedUser currentUser) {
        this.buchungService = buchungService;
        this.userService = userService;
        this.dozentService = dozentService;
        this.currentUser = currentUser;

        setupGrid();
        add(grid);

    }
    private void setupGrid() {
        grid = new Grid<>();

        grid.addColumn(Buchung::getRoom).setHeader("Room");
        grid.addColumn(Buchung::getDate).setHeader("Date");
        grid.addColumn(Buchung::getStartZeit).setHeader("Startzeit");
        grid.addColumn(Buchung::getEndZeit).setHeader("Endzeit");

        User userData = currentUser.get().get();

        List<Dozent> dozent = dozentService.findByVornameAndNachname(userData.getFirstName(), userData.getLastName());

        Set<Buchung> dozentBuchungen = buchungService.findAllByDozent(dozent.getFirst());

        grid.setItems(dozentBuchungen);
    }

}

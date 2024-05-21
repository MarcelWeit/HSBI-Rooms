package com.example.application.views;

import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.Set;

@Route(value = "veranstaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung", "DOZENT"})
@RolesAllowed({"ADMIN", "FBPlanung", "DOZENT"})
@Uses(Icon.class)
@PageTitle("Veranstaltungen")
public class VeranstaltungView extends VerticalLayout {

    private VeranstaltungService veranstaltungService;
    private Grid<Veranstaltung> grid;

    public VeranstaltungView(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;

        addGrid();
    }
    private void addGrid() {
        Set<Veranstaltung> veranstaltungSet = veranstaltungService.findAll();

        grid = new Grid<>();
        grid.setItems(veranstaltungSet);

        grid.addColumn(Veranstaltung::getId).setHeader("ID");
        grid.addColumn(Veranstaltung::getBezeichnung).setHeader("Bezeichnung");
        grid.addColumn(Veranstaltung::getDozent).setHeader("Dozent");
        grid.addColumn(Veranstaltung::getFachbereich).setHeader("Fachbereich");
        grid.addColumn(Veranstaltung::getTeilnehmerzahl).setHeader("Teilnehmer");


        add(grid);

    }
}

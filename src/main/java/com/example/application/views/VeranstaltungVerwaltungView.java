package com.example.application.views;

import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "veranstaltungVerwaltung-crud", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "FBPlanung"})
@Uses(Icon.class)
@PageTitle("VeranstaltungsVerwaltung")
public class VeranstaltungVerwaltungView {

    private final VeranstaltungService veranstaltungService;

    public VeranstaltungVerwaltungView(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;
    }

}

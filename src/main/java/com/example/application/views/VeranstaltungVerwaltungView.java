package com.example.application.views;

import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

@Route(value = "veranstaltungVerwaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung"})
@RolesAllowed({"ADMIN", "FBPlanung"})
@Uses(Icon.class)
@PageTitle("VeranstaltungsVerwaltung")
public class VeranstaltungVerwaltungView extends VerticalLayout {

    private final VeranstaltungService veranstaltungService;

    public VeranstaltungVerwaltungView(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;
    }

}

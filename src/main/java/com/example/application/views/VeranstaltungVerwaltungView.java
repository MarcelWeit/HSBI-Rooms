package com.example.application.views;

import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Veranstaltungen verwalten")
@RolesAllowed({"ADMIN", "FBPlanung"})
public class VeranstaltungVerwaltungView {

    private final VeranstaltungService veranstaltungService;

    public VeranstaltungVerwaltungView(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;
    }

}

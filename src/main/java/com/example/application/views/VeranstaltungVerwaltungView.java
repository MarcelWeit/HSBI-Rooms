package com.example.application.views;

import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Veranstaltungen verwalten")
@RolesAllowed({"ADMIN", "FBPlanung"})
public class VeranstaltungVerwaltungView {

}

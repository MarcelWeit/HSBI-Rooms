package com.example.application.views;

import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

@Route(value = "veranstaltungVerwaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung"})
@RolesAllowed({"ADMIN", "FBPlanung"})
@Uses(Icon.class)
@PageTitle("Veranstaltungsen")
public class VeranstaltungVerwaltungView extends VerticalLayout {

    private final VeranstaltungService veranstaltungService;

    private Crud crud;

    public VeranstaltungVerwaltungView(VeranstaltungService veranstaltungService) {
        this.veranstaltungService = veranstaltungService;

        crud = new Crud<>(Veranstaltung.class, createEditor());

        this.createComponents();
    }

    private void createComponents() {

    }
    private CrudEditor<Veranstaltung> createEditor() {

        TextField veranstaltung = new TextField("VeranstaltungsID");
        TextField bezeichnung = new TextField("Bezeichnung");

        ComboBox dozent = new ComboBox("Dozent");

    }

}

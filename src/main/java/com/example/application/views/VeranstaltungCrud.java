package com.example.application.views;

import com.example.application.data.dataProvider.VeranstaltungDataProvider;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "show-veranstaltung", layout = MainLayout.class)
@PageTitle("Veranstaltungen anzeigen")
@RolesAllowed({"ADMIN", "DOZENT", "FBPlanung"})
@Uses(Icon.class)
public class VeranstaltungCrud extends VerticalLayout {

    private final VeranstaltungService veranstaltungService;

    private final Crud<Veranstaltung> crud;

    public VeranstaltungCrud(VeranstaltungService veranstaltungService) {
        addClassNames("veranstaltung-view");
        this.veranstaltungService = veranstaltungService;

        crud = new Crud<>(Veranstaltung.class, createEditor());

        setupGrid();
        setupLanguage();
        setupDataProvider();

        add(crud);
    }

    private void setupGrid() {
        Grid<Veranstaltung> grid = crud.getGrid();
    }

    private CrudEditor<Veranstaltung> createEditor() {
        return new BinderCrudEditor<>(new Binder<>(Veranstaltung.class));
    }

    private void setupDataProvider() {
        VeranstaltungDataProvider dataProvider = new VeranstaltungDataProvider(veranstaltungService);
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(deleteEvent -> {
            dataProvider.delete(deleteEvent.getItem());
            dataProvider.refreshAll();
        });
        crud.addSaveListener(saveEvent -> {
            dataProvider.save(saveEvent.getItem());
            dataProvider.refreshAll();
        });
    }

    private void setupLanguage() {
        CrudI18n i18n = CrudI18n.createDefault();
        i18n.setNewItem("Neuer Eintrag");
        i18n.setEditItem("Bearbeiten");
        i18n.setSaveItem("Speichern");
        i18n.setCancel("Abbrechen");
        i18n.setDeleteItem("Löschen");
        i18n.setEditLabel("Bearbeiten");

        CrudI18n.Confirmations.Confirmation delete = i18n.getConfirm()
                .getDelete();
        delete.setTitle("Eintrag löschen");
        delete.setContent(
                "Sind Sie sicher, dass Sie diesen Eintrag löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.");
        delete.getButton().setConfirm("Bestätigen");
        delete.getButton().setDismiss("Zurück");

        CrudI18n.Confirmations.Confirmation cancel = i18n.getConfirm()
                .getCancel();
        cancel.setTitle("Änderungen verwerfen");
        cancel.setContent("Sie haben Änderungen an diesem Eintrag vorgenommen, die noch nicht gespeichert wurden.");
        cancel.getButton().setConfirm("Verwerfen");
        cancel.getButton().setDismiss("Zurück");

        crud.setI18n(i18n);
    }

}


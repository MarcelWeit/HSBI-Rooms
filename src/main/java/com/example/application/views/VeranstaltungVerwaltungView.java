package com.example.application.views;

import com.example.application.data.dataProvider.RoomDataProvider;
import com.example.application.data.dataProvider.VeranstaltungDataProvider;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Room;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.DozentService;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

@Route(value = "veranstaltungVerwaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung"})
@RolesAllowed({"ADMIN", "FBPlanung"})
@Uses(Icon.class)
@PageTitle("Veranstaltungen")
public class VeranstaltungVerwaltungView extends VerticalLayout {

    private final VeranstaltungService veranstaltungService;
    private final DozentService dozentService;

    private final Crud<Veranstaltung> crud;

    public VeranstaltungVerwaltungView(VeranstaltungService veranstaltungService, DozentService dozentService) {
        this.veranstaltungService = veranstaltungService;
        this.dozentService = dozentService;

        crud = new Crud<>(Veranstaltung.class, createEditor());

        //setupGrid();
        setupDataProvider();
        setupLanguage();

        add(crud);

    }

    private CrudEditor<Veranstaltung> createEditor() {

        TextField veranstaltung = new TextField("VeranstaltungsID");
        TextField bezeichnung = new TextField("Bezeichnung");

        ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
        dozent.setItems(dozentService.findAll());
        dozent.setItemLabelGenerator(Dozent::getNachname);

        IntegerField teiln = new IntegerField("Teilnehmerzahl");

        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);

        FormLayout form = new FormLayout(veranstaltung, bezeichnung, dozent, teiln, fachbereich);

        Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);
        binder.forField(veranstaltung).asRequired().bind(Veranstaltung::getId, Veranstaltung::setId);
        binder.forField(bezeichnung).asRequired().bind(Veranstaltung::getBezeichnung, Veranstaltung::setBezeichnung);
        binder.forField(dozent).asRequired().bind(Veranstaltung::getDozent, Veranstaltung::setDozent);
        binder.forField(teiln).asRequired().bind(Veranstaltung::getTeilnehmerzahl, Veranstaltung::setTeilnehmerzahl);
        binder.forField(fachbereich).asRequired().bind(Veranstaltung::getFachbereich, Veranstaltung::setFachbereich);

        return new BinderCrudEditor<>(binder, form);

    }
    private void setupGrid() {
        Grid<Veranstaltung> grid = crud.getGrid();

        grid.getColumnByKey("vaadin-crud-edit-column").setFrozenToEnd(true);

        grid.setColumnOrder(
                grid.getColumnByKey("veranstaltung"),
                grid.getColumnByKey("bezeichnung"),
                grid.getColumnByKey("dozent"),
                grid.getColumnByKey("teilnehmerzahl"),
                grid.getColumnByKey("fachbereich"),
                grid.getColumnByKey("vaadin-crud-edit-column"));
    }
    private void setupDataProvider() {
        VeranstaltungDataProvider dataProvider = new VeranstaltungDataProvider(veranstaltungService);
        crud.setDataProvider(dataProvider);

        crud.addDeleteListener(deleteEvent -> {
            dataProvider.deleteVeranstaltung(deleteEvent.getItem());
            dataProvider.refreshAll();
        });
        crud.addSaveListener(saveEvent -> {
            dataProvider.saveVeranstaltung(saveEvent.getItem());
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

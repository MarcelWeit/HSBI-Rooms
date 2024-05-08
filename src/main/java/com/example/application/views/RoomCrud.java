package com.example.application.views;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Raumtyp;
import com.example.application.data.entities.Room;
import com.example.application.services.AusstattungService;
import com.example.application.services.RoomDataProvider;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "room-crud", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@PageTitle("Räume")
public class RoomCrud extends Div {

    private final AusstattungService ausstattungService;
    private final RoomService roomService;

    private final Crud<Room> crud;

    private final String FACHBEREICH = "fachbereich";
    private final String POSITION = "position";
    private final String TYP = "typ";
    private final String CAPACITY = "capacity";
    private final String AUSSTATTUNG = "ausstattung";
    private final String REFNR = "refNr";
    private final String EDIT_COLUMN = "vaadin-crud-edit-column";

    public RoomCrud(AusstattungService ausstattungService, RoomService roomService) {
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;

        crud = new Crud<>(Room.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupLanguage();

        add(crud);
    }

    private CrudEditor<Room> createEditor() {

        TextField refNr = new TextField("ReferenzBezeichnung");

        IntegerField kapa = new IntegerField("Kapazität");
        MultiSelectComboBox<Ausstattung> ausstattung = new MultiSelectComboBox<>("Ausstattung");
        ausstattung.setItems(ausstattungService.findAll());
        ausstattung.setItemLabelGenerator(Ausstattung::getBez);
        ComboBox<Raumtyp> typ = new ComboBox<>("Raumtyp");
        typ.setItems(Raumtyp.values());
        typ.setItemLabelGenerator(Raumtyp::toString);
        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);
        fachbereich.setRequired(true);
        TextField position = new TextField("Position");

        FormLayout form = new FormLayout(refNr, kapa, ausstattung, typ, fachbereich, position);

        Binder<Room> binder = new Binder<>(Room.class);
        binder.forField(kapa).asRequired().bind(Room::getCapacity, Room::setCapacity);
        binder.forField(ausstattung).bind(Room::getAusstattung, Room::setAusstattung);
        binder.forField(typ).asRequired().bind(Room::getTyp, Room::setTyp);
        binder.forField(fachbereich).asRequired().bind(Room::getFachbereich, Room::setFachbereich);
        binder.forField(position).asRequired().bind(Room::getPosition, Room::setPosition);
        binder.forField(refNr).asRequired()
                .withValidator(refNrValue -> refNrValue.matches("^[a-zA-Z]{1}.{0,3}$"),
                        "ReferenzBezeichnung muss mit einem Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
                .bind(Room::getRefNr, Room::setRefNr);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<Room> grid = crud.getGrid();

        grid.removeColumn(grid.getColumnByKey("id"));
        grid.getColumnByKey(CAPACITY).setHeader("Kapazität");
        grid.getColumnByKey(EDIT_COLUMN).setFrozenToEnd(true);

        // Anordnung der Spalten
        grid.setColumnOrder(grid.getColumnByKey(REFNR),
                grid.getColumnByKey(FACHBEREICH),
                grid.getColumnByKey(AUSSTATTUNG),
                grid.getColumnByKey(TYP),
                grid.getColumnByKey(CAPACITY),
                grid.getColumnByKey(POSITION),
                grid.getColumnByKey(EDIT_COLUMN));
    }

    private void setupDataProvider() {
        RoomDataProvider dataProvider = new RoomDataProvider(roomService);
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

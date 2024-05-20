package com.example.application.views;

import com.example.application.data.dataProvider.RoomDataProvider;
import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Raumtyp;
import com.example.application.data.entities.Room;
import com.example.application.services.AusstattungService;
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
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

/**
 * @author marcel weithoener
 */
@Route(value = "room-crud", layout = MainLayout.class)
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@PageTitle("Räume")
public class RoomCrud extends Div {

    private final AusstattungService ausstattungService;
    private final RoomService roomService;

    private final Crud<Room> crud;
    private final Binder<Room> binder = new Binder<>(Room.class);
    private final TextField refNr = new TextField("ReferenzBezeichnung");

    private final String FACHBEREICH = "fachbereich";
    private final String POSITION = "position";
    private final String TYP = "typ";
    private final String CAPACITY = "capacity";
    private final String AUSSTATTUNG = "ausstattung";
    private final String REFNR = "refNr";
    private final String EDIT_COLUMN = "vaadin-crud-edit-column";

    /**
     * @param ausstattungService Service für Ausstattung
     * @param roomService        Service für Räume
     */
    public RoomCrud(AusstattungService ausstattungService, RoomService roomService) {
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;

        crud = new Crud<>(Room.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupLanguage();

        crud.setMinHeight("80vh");

        add(crud);

        crud.getNewButton().getElement().addEventListener("click", event -> {
            refNr.setEnabled(true);
            binder.forField(refNr).asRequired()
                    .withValidator(refNrValue -> refNrValue.matches("^[a-zA-Z]{1}.{0,3}$"),
                            "ReferenzBezeichnung muss mit einem Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
                    .withValidator(refNrValue -> !roomService.refNrExists(refNrValue),
                            "ReferenzBezeichnung already exists")
                    .bind(Room::getRefNr, Room::setRefNr);
        });
        crud.getCancelButton().getElement().addEventListener("click", event -> {
            refNr.setEnabled(false);
        });
    }

    /**
     * Erstellen des Editors
     *
     * @return BinderCrudEditor<>(binder, form)
     */
    private CrudEditor<Room> createEditor() {

        refNr.setEnabled(false);

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

        binder.forField(kapa).asRequired().bind(Room::getCapacity, Room::setCapacity);
        binder.forField(ausstattung).asRequired().bind(Room::getAusstattung, Room::setAusstattung);
        binder.forField(typ).asRequired().bind(Room::getTyp, Room::setTyp);
        binder.forField(fachbereich).asRequired().bind(Room::getFachbereich, Room::setFachbereich);
        binder.forField(position).asRequired().bind(Room::getPosition, Room::setPosition);
        binder.forField(refNr).asRequired()
                .withValidator(refNrValue -> refNrValue.matches("^[a-zA-Z]{1}.{0,3}$"),
                        "ReferenzBezeichnung muss mit einem Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
                .bind(Room::getRefNr, Room::setRefNr);
        return new BinderCrudEditor<>(binder, form);
    }

    /**
     * Grid anpassen
     */
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

    /**
     * Datenprovider für das CRUD-Objekt setzen
     */
    private void setupDataProvider() {
        RoomDataProvider dataProvider = new RoomDataProvider(roomService);
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(deleteEvent -> {
            dataProvider.delete(deleteEvent.getItem());
            dataProvider.refreshAll();
        });
        crud.addSaveListener(saveEvent -> {
            if (roomService.refNrExists(saveEvent.getItem().getRefNr())) {
                System.out.println("Error");
            } else {
                dataProvider.save(saveEvent.getItem());
                dataProvider.refreshAll();
            }
        });
    }

    /**
     * Text der Vaadin Komponenten auf Deutsch setzen
     */
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

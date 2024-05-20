package com.example.application.views;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Raumtyp;
import com.example.application.data.entities.Room;
import com.example.application.services.AusstattungService;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.Optional;

@Route(value = "raumverwaltung", layout = MainLayout.class)
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@PageTitle("Räume verwalten")
public class RoomCrud extends VerticalLayout {

    private final AusstattungService ausstattungService;
    private final RoomService roomService;

    private final Grid<Room> roomGrid = new Grid<>(Room.class);
    private final Binder<Room> roomBinder = new Binder<>(Room.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    public RoomCrud(AusstattungService ausstattungService, RoomService roomService) {
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;

        setupButtons();
        setupGrid();
        add(buttonLayout, roomGrid);
    }

    private void setupGrid() {
        roomGrid.setItems(roomService.findAll());
        roomGrid.removeColumn(roomGrid.getColumnByKey("id"));
        roomGrid.getColumnByKey("capacity")
                .setAutoWidth(true).setFlexGrow(0)
                .setHeader("Kapazität");
        roomGrid.getColumnByKey("refNr").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("fachbereich").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("ausstattung").setAutoWidth(true);
        roomGrid.getColumnByKey("typ").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("position").setAutoWidth(true).setFlexGrow(0);

        roomGrid.setColumnOrder(roomGrid.getColumnByKey("refNr"),
                roomGrid.getColumnByKey("fachbereich"),
                roomGrid.getColumnByKey("position"),
                roomGrid.getColumnByKey("typ"),
                roomGrid.getColumnByKey("capacity"),
                roomGrid.getColumnByKey("ausstattung"));

        roomGrid.setMinHeight("70vh");
    }

    private void openEditDialog() {
        Optional<Room> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedRoom.isEmpty()) {
            Notification.show("Bitte wählen Sie einen Raum aus", 2000, Notification.Position.MIDDLE);
        } else {
            openEditCreateDialog(selectedRoom);
        }

    }

    private void openEditCreateDialog(Optional<Room> selectedRoom) {
        Dialog dialog = new Dialog();
        FormLayout form = new FormLayout();

        TextField refNr = new TextField("Referenznummer");

        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());

        TextField position = new TextField("Position");

        ComboBox<Raumtyp> raumtyp = new ComboBox<>("Typ");
        raumtyp.setItems(Raumtyp.values());

        IntegerField capacity = new IntegerField("Kapazität");
        capacity.setMin(1);
        capacity.setMax(1000);
        capacity.setValue(50);
        capacity.setStepButtonsVisible(true);

        MultiSelectComboBox<Ausstattung> ausstattung = new MultiSelectComboBox<>("Ausstattung");
        ausstattung.setItems(ausstattungService.findAll());

        form.add(refNr, fachbereich, position, raumtyp, capacity, ausstattung);
        dialog.add(form);

        roomBinder.forField(capacity).asRequired().bind(Room::getCapacity, Room::setCapacity);
        roomBinder.forField(ausstattung).bind(Room::getAusstattung, Room::setAusstattung);
        roomBinder.forField(raumtyp).asRequired().bind(Room::getTyp, Room::setTyp);
        roomBinder.forField(fachbereich).asRequired("Bitte einen Fachbereich auswählen").bind(Room::getFachbereich, Room::setFachbereich);
        roomBinder.forField(position).asRequired("Bitte eine Position angeben").bind(Room::getPosition, Room::setPosition);

        if (selectedRoom.isEmpty()) {
            roomBinder.forField(refNr).asRequired("Bitte eine Referenznummer angeben")
                    .withValidator(refNrValue -> refNrValue.matches("^[a-zA-Z]{1}.{0,3}$"),
                            "Die Referenznummer muss mit einem Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
                    .withValidator(refNrValue -> !roomService.refNrExists(refNrValue),
                            "Referenznummer existiert bereits")
                    .bind(Room::getRefNr, Room::setRefNr);
        } else {
            roomBinder.forField(refNr).bind(Room::getRefNr, Room::setRefNr);
        }


        if (selectedRoom.isPresent()) {
            roomBinder.readBean(selectedRoom.get());
            refNr.setEnabled(false);
            refNr.setErrorMessage(null);
            refNr.setInvalid(false);
        }

        Button cancelButton = new Button("Abbrechen", event -> dialog.close());
        Button saveButton = new Button("Speichern");
        saveButton.addClickListener(event -> {
            Room room = new Room();
            if (roomBinder.writeBeanIfValid(room)) {
                roomService.save(room);
                roomGrid.setItems(roomService.findAll());
                dialog.close();
            } else {
                Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
            }
        });

        dialog.getFooter().add(cancelButton, saveButton);

        dialog.open();
    }

    private void openDeleteDialog(RoomService roomService) {
        Optional<Room> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedRoom.isEmpty()) {
            Notification.show("Bitte wählen Sie einen Raum aus", 2000, Notification.Position.MIDDLE);
        } else {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("Raum " + selectedRoom.get().getRefNr() + " löschen?");
            confirmDeleteDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden.");

            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmButtonTheme("error primary");

            confirmDeleteDialog.setConfirmButton("Löschen", event -> {
                roomService.delete(selectedRoom.get());
                roomGrid.setItems(roomService.findAll());
                confirmDeleteDialog.close();
            });

            confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());

            confirmDeleteDialog.open();
        }
    }

    private void setupButtons() {
        Button addRoomButton = new Button("Raum hinzufügen", new Icon(VaadinIcon.PLUS));
        addRoomButton.addClickListener(e -> openEditCreateDialog(Optional.empty()));

        Button editRoomButton = new Button("Raum bearbeiten", new Icon(VaadinIcon.EDIT));
        editRoomButton.addClickListener(e -> openEditDialog());

        Button deleteRoomButton = new Button("Raum löschen", new Icon(VaadinIcon.TRASH));
        deleteRoomButton.addClickListener(e -> openDeleteDialog(roomService));

        buttonLayout.add(addRoomButton, editRoomButton, deleteRoomButton);
    }
}

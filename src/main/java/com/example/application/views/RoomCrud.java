package com.example.application.views;

import com.example.application.comparator.refNrComparator;
import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Raumtyp;
import com.example.application.data.entities.Room;
import com.example.application.services.AusstattungService;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Route(value = "raumverwaltung", layout = MainLayout.class)
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@PageTitle("Räume verwalten")
public class RoomCrud extends VerticalLayout {

    private final AusstattungService ausstattungService;
    private final RoomService roomService;

    private final Grid<Room> roomGrid = new Grid<>(Room.class, false);
    private final Binder<Room> roomBinder = new Binder<>(Room.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    public RoomCrud(AusstattungService ausstattungService, RoomService roomService) {
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;

        setupButtons();
        setupGrid();
        add(buttonLayout, roomGrid);
    }

    private static Component createStringFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }

    private void setupGrid() {
        GridListDataView<Room> dataView = roomGrid.setItems(roomService.findAll());

        roomGrid.addColumn(Room::getRefNr).setHeader("Referenznummer")
                .setComparator(new refNrComparator())
                .setKey("refNr");
        roomGrid.addColumn(Room::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        roomGrid.addColumn(Room::getPosition).setHeader("Position").setKey("position");
        roomGrid.addColumn(Room::getTyp).setHeader("Typ").setKey("typ");
        roomGrid.addColumn(Room::getCapacity).setHeader("Kapazität").setKey("capacity");
        roomGrid.addColumn(Room::getAusstattungAsString).setHeader("Ausstattung").setKey("ausstattung");

        roomGrid.getColumnByKey("capacity")
                .setAutoWidth(true).setFlexGrow(0)
                .setHeader("Kapazität");
        roomGrid.getColumnByKey("refNr").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("fachbereich").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("ausstattung").setAutoWidth(true);
        roomGrid.getColumnByKey("typ").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("position").setAutoWidth(true).setFlexGrow(0);

        // Sort by reference number by default
        GridSortOrder<Room> sortOrder = new GridSortOrder<>(roomGrid.getColumnByKey("refNr"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<Room>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);
        roomGrid.sort(sortOrders);

        roomGrid.setMinHeight("80vh");

        setupFilter(dataView);
    }

    private void setupFilter(GridListDataView<Room> dataView) {
        RoomFilter roomFilter = new RoomFilter(dataView);

        roomGrid.getHeaderRows().clear();
        HeaderRow headerRow = roomGrid.appendHeaderRow();

        Consumer<Fachbereich> fachbereichFilterChangeConsumer = roomFilter::setFachbereich;
        ComboBox<Fachbereich> fachbereichComboBox = new ComboBox<>();
        fachbereichComboBox.setItems(Fachbereich.values());
        fachbereichComboBox.setClearButtonVisible(true);
        fachbereichComboBox.addValueChangeListener(e -> fachbereichFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(roomGrid.getColumnByKey("fachbereich")).setComponent(fachbereichComboBox);

        Consumer<Raumtyp> raumtypFilterChangeConsumer = roomFilter::setRaumtyp;
        ComboBox<Raumtyp> raumtypComboBox = new ComboBox<>();
        raumtypComboBox.setItems(Raumtyp.values());
        raumtypComboBox.setClearButtonVisible(true);
        raumtypComboBox.addValueChangeListener(e -> raumtypFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(roomGrid.getColumnByKey("typ")).setComponent(raumtypComboBox);

        Consumer<Set<Ausstattung>> ausstattungFilterChangeConsumer = roomFilter::setAusstattung;
        MultiSelectComboBox<Ausstattung> ausstattungMultiSelectComboBox = new MultiSelectComboBox<>();
        ausstattungMultiSelectComboBox.setItems(ausstattungService.findAll());
        ausstattungMultiSelectComboBox.setClearButtonVisible(true);
        ausstattungMultiSelectComboBox.addValueChangeListener(e -> ausstattungFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(roomGrid.getColumnByKey("ausstattung")).setComponent(ausstattungMultiSelectComboBox);

        Consumer<Integer> capacityFilterChangeConsumer = roomFilter::setCapacity;
        IntegerField capacityField = new IntegerField();
        capacityField.setMin(0);
        capacityField.setMax(1000);
        capacityField.setValue(0);
        capacityField.setStepButtonsVisible(true);
        capacityField.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                capacityField.setValue(0);
            }
            capacityFilterChangeConsumer.accept(e.getValue());
        });
        headerRow.getCell(roomGrid.getColumnByKey("capacity")).setComponent(capacityField);

        headerRow.getCell(roomGrid.getColumnByKey("refNr")).setComponent(createStringFilterHeader(roomFilter::setRefNr));
        headerRow.getCell(roomGrid.getColumnByKey("position")).setComponent(createStringFilterHeader(roomFilter::setPosition));

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
                    .withValidator(refNrValue -> refNrValue.matches("^[A-Z]{1}.{0,3}$"),
                            "Die Referenznummer muss mit einem großen Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
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

    private static class RoomFilter {
        private final GridListDataView<Room> dataView;

        private String refNr;
        private Fachbereich fachbereich;
        private String position;
        private Raumtyp raumtyp;
        private Set<Ausstattung> ausstattung;
        private int capacity;

        public RoomFilter(GridListDataView<Room> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
            this.dataView.refreshAll();
        }

        public void setRefNr(String refNr) {
            this.refNr = refNr;
            this.dataView.refreshAll();
        }

        public void setFachbereich(Fachbereich fachbereich) {
            this.fachbereich = fachbereich;
            this.dataView.refreshAll();
        }

        public void setPosition(String pos) {
            this.position = pos;
            this.dataView.refreshAll();
        }

        public void setRaumtyp(Raumtyp raumtyp) {
            this.raumtyp = raumtyp;
            this.dataView.refreshAll();
        }

        public void setAusstattung(Set<Ausstattung> ausstattung) {
            this.ausstattung = ausstattung;
            this.dataView.refreshAll();
        }

        public boolean test(Room room) {
            boolean matchesRefNr = matches(room.getRefNr(), refNr);
            boolean matchesFachbereich = true;
            if (fachbereich != null) {
                matchesFachbereich = matches(room.getFachbereich().toString(), fachbereich.toString());
            }
            boolean matchesPosition = matches(room.getPosition(), position);
            boolean matchesRaumtyp = true;
            if (raumtyp != null) {
                matchesRaumtyp = matches(room.getTyp().toString(), raumtyp.toString());
            }
            boolean matchesAusstattung = true;
            if (ausstattung != null) {
                matchesAusstattung = room.getAusstattung().containsAll(ausstattung);
            }
            boolean matchesCapacity = room.getCapacity() >= capacity;

            return matchesRefNr && matchesFachbereich && matchesPosition && matchesRaumtyp && matchesAusstattung && matchesCapacity;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}

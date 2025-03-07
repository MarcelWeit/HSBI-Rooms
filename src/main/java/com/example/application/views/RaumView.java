package com.example.application.views;

import com.example.application.comparator.refNrComparator;
import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Raum;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Raumtyp;
import com.example.application.data.enums.Role;
import com.example.application.dialogs.BelegungRaumKalenderwocheDialog;
import com.example.application.dialogs.BuchungAnlegenBearbeitenDialog;
import com.example.application.dialogs.BuchungenAnzeigenDialog;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
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

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * View, um Räume zu verwalten (hinzufügen, bearbeiten, löschen).
 * Zudem können Räume gebucht werden, Buchungen angezeigt und die Verfügbarkeit in einer Kalenderwoche angezeigt werden.
 *
 * @author Marcel Weithoener
 */
@Route(value = "raumverwaltung", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
@Uses(Icon.class)
@PageTitle("Räume verwalten")
public class RaumView extends VerticalLayout {

    private final AusstattungService ausstattungService;
    private final RaumService roomService;
    private final DozentService dozentService;
    private final VeranstaltungService veranstaltungService;
    private final BuchungService buchungService;

    private final Grid<Raum> roomGrid = new Grid<>(Raum.class, false);
    private final Binder<Raum> roomBinder = new Binder<>(Raum.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    private final AuthenticatedUser currentUser;
    private final RaumService raumService;
    private GridListDataView<Raum> dataView;
    private HeaderRow headerRow;

    /**
     * Konstruktor der Klasse RaumView
     */
    public RaumView(AusstattungService ausstattungService, RaumService roomService, DozentService dozentService,
                    VeranstaltungService veranstaltungService, BuchungService buchungService, AuthenticatedUser currentUser, RaumService raumService) {
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;
        this.dozentService = dozentService;
        this.veranstaltungService = veranstaltungService;
        this.buchungService = buchungService;

        this.currentUser = currentUser;

        setupButtons();
        setupGrid();
        updateGrid();
        add(buttonLayout, roomGrid);
        this.raumService = raumService;
    }

    /**
     * Erstellt ein Textfeld für die Filterung der Räume
     *
     * @param filterChangeConsumer Consumer für die Filterung
     * @return Textfeld für die Filterung
     */
    private static Component createStringFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }

    /**
     * Erstellt das Grid für die Räume
     */
    private void setupGrid() {

        roomGrid.addColumn(Raum::getRefNr).setHeader("Referenznummer")
                .setComparator(new refNrComparator())
                .setKey("refNr");
        roomGrid.addColumn(Raum::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        roomGrid.addColumn(Raum::getTyp).setHeader("Typ").setKey("typ");
        roomGrid.addColumn(Raum::getCapacity).setHeader("Kapazität").setKey("capacity");
        roomGrid.addColumn(Raum::getAusstattungAsString).setHeader("Ausstattung").setKey("ausstattung");
        roomGrid.addColumn(Raum::getPosition).setHeader("Position").setKey("position");

        roomGrid.getColumnByKey("capacity")
                .setAutoWidth(true).setFlexGrow(0)
                .setHeader("Kapazität");
        roomGrid.getColumnByKey("refNr").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("fachbereich").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("ausstattung").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("typ").setAutoWidth(true).setFlexGrow(0);
        roomGrid.getColumnByKey("position").setAutoWidth(true);

        // Sort by reference number by default
        GridSortOrder<Raum> sortOrder = new GridSortOrder<>(roomGrid.getColumnByKey("refNr"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<Raum>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);
        roomGrid.sort(sortOrders);

        roomGrid.setMinHeight("80vh");

    }

    /**
     * Erstellt die Buttons für die Raumverwaltung
     */
    private void setupButtons() {
        Button addRoomButton = new Button("Raum hinzufügen", new Icon(VaadinIcon.PLUS));
        addRoomButton.addClickListener(e -> openEditCreateDialog(null));

        Button editRoomButton = new Button("Raum bearbeiten", new Icon(VaadinIcon.EDIT));
        editRoomButton.addClickListener(e -> {
            Optional<Raum> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
            if (selectedRoom.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Raum aus", 2000, Notification.Position.MIDDLE);
            } else {
                openEditCreateDialog(selectedRoom.get());
            }
        });

        Button deleteRoomButton = new Button("Raum löschen", new Icon(VaadinIcon.TRASH));
        deleteRoomButton.addClickListener(e -> openDeleteDialog());

        Button bookRoomButton = new Button("Raum buchen", new Icon(VaadinIcon.PLUS));
        bookRoomButton.addClickListener(click -> openRoomBookDialog());

        Button showBookingsButton = new Button("Buchungen anzeigen", new Icon(VaadinIcon.CALENDAR));
        showBookingsButton.addClickListener(click -> openShowBookingsDialog());

        Button showWeekBookingButton = new Button("Verfügbarkeit", new Icon(VaadinIcon.CALENDAR));
        showWeekBookingButton.addClickListener(click -> {
            Optional<Raum> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
            if (selectedRoom.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Raum aus", 2000, Notification.Position.MIDDLE);
            } else {
                BelegungRaumKalenderwocheDialog belegungWocheDialog = new BelegungRaumKalenderwocheDialog(selectedRoom.get(), buchungService);
                belegungWocheDialog.open();
            }
        });

        if (currentUser.get().isPresent()) {
            buttonLayout.add(addRoomButton, editRoomButton, deleteRoomButton, bookRoomButton, showBookingsButton, showWeekBookingButton);
            // Dozent kann keine Räume hinzufügen, bearbeiten oder löschen
            if (currentUser.get().get().getRoles().contains(Role.DOZENT)) {
                buttonLayout.remove(addRoomButton, editRoomButton, deleteRoomButton);
            } else if (currentUser.get().get().getRoles().contains(Role.FBPLANUNG)) {
                buttonLayout.remove(addRoomButton, deleteRoomButton);
            }
        }
    }

    /**
     * Erstellt die Filter für die Räume
     */
    private void setupFilter() {
        RoomFilter roomFilter = new RoomFilter(dataView);

        if (headerRow == null) {
            headerRow = roomGrid.appendHeaderRow();
        }

        headerRow.getCell(roomGrid.getColumnByKey("refNr")).setComponent(createStringFilterHeader(roomFilter::setRefNr));

        Consumer<Fachbereich> fachbereichFilterChangeConsumer = roomFilter::setFachbereich;
        ComboBox<Fachbereich> fachbereichComboBox = new ComboBox<>();
        fachbereichComboBox.setWidthFull();
        if (currentUser.get().isPresent() && !currentUser.get().get().getRoles().contains(Role.ADMIN)) {
            fachbereichComboBox.setItems(currentUser.get().get().getFachbereich());
            fachbereichComboBox.setValue(currentUser.get().get().getFachbereich());
            fachbereichComboBox.setEnabled(false);
        } else {
            fachbereichComboBox.setItems(Fachbereich.values());
        }
        fachbereichComboBox.setClearButtonVisible(true);
        fachbereichComboBox.addValueChangeListener(e -> fachbereichFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(roomGrid.getColumnByKey("fachbereich")).setComponent(fachbereichComboBox);

        Consumer<Raumtyp> raumtypFilterChangeConsumer = roomFilter::setRaumtyp;
        ComboBox<Raumtyp> raumtypComboBox = new ComboBox<>();
        raumtypComboBox.setItems(Raumtyp.values());
        raumtypComboBox.setClearButtonVisible(true);
        raumtypComboBox.addValueChangeListener(e -> raumtypFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(roomGrid.getColumnByKey("typ")).setComponent(raumtypComboBox);

        Consumer<Integer> capacityFilterChangeConsumer = roomFilter::setCapacity;
        IntegerField capacityField = new IntegerField();
        capacityField.setTooltipText("Minimale Kapazität");
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

        Consumer<Set<Ausstattung>> ausstattungFilterChangeConsumer = roomFilter::setAusstattung;
        MultiSelectComboBox<Ausstattung> ausstattungMultiSelectComboBox = new MultiSelectComboBox<>();
        ausstattungMultiSelectComboBox.setItems(ausstattungService.findAll());
        ausstattungMultiSelectComboBox.setClearButtonVisible(true);
        ausstattungMultiSelectComboBox.addValueChangeListener(e -> ausstattungFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(roomGrid.getColumnByKey("ausstattung")).setComponent(ausstattungMultiSelectComboBox);

        headerRow.getCell(roomGrid.getColumnByKey("position")).setComponent(createStringFilterHeader(roomFilter::setPosition));

    }

    /**
     * Öffnet den Dialog zum Bearbeiten oder Erstellen eines Raumes
     *
     * @param selectedRoom Optionaler Raum
     */
    private void openEditCreateDialog(Raum selectedRoom) {
        Dialog dialog = new Dialog();
        dialog.setId("raum-dialog");
        dialog.setMaxWidth("25vw");
        dialog.setMinWidth("200px");
        FormLayout form = new FormLayout();

        TextField refNr = new TextField("Referenznummer");

        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());

        TextField position = new TextField("Position");
        position.setId("textfield-position");

        ComboBox<Raumtyp> raumtyp = new ComboBox<>("Typ");
        raumtyp.setItems(Raumtyp.values());

        IntegerField capacity = new IntegerField("Kapazität");
        capacity.setMin(1);
        capacity.setMax(1000);
        capacity.setValue(50);
        capacity.setStepButtonsVisible(true);
        capacity.setId("integerfield-capacity");

        MultiSelectComboBox<Ausstattung> ausstattung = new MultiSelectComboBox<>("Ausstattung");
        ausstattung.setItems(ausstattungService.findAll());

        form.add(refNr, fachbereich, position, raumtyp, capacity, ausstattung);
        dialog.add(form);

        roomBinder.forField(capacity).asRequired().bind(Raum::getCapacity, Raum::setCapacity);
        roomBinder.forField(ausstattung).bind(Raum::getAusstattung, Raum::setAusstattung);
        roomBinder.forField(raumtyp).asRequired("Bitte einen Typ auswählen").bind(Raum::getTyp, Raum::setTyp);
        roomBinder.forField(fachbereich).asRequired("Bitte einen Fachbereich auswählen").bind(Raum::getFachbereich, Raum::setFachbereich);
        roomBinder.forField(position).asRequired("Bitte eine Position angeben").bind(Raum::getPosition, Raum::setPosition);

        roomBinder.forField(refNr).asRequired("Bitte eine Referenznummer angeben")
                .withValidator(refNrValue -> refNrValue.matches("^[A-Z].{0,3}$"),
                        "Die Referenznummer muss mit einem großen Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
                .bind(Raum::getRefNr, Raum::setRefNr);

        if (selectedRoom != null) {
            roomBinder.readBean(selectedRoom);
            refNr.setEnabled(false);
        }

        Button cancelButton = new Button("Abbrechen", event -> dialog.close());
        Button saveButton = new Button("Speichern");
        saveButton.addClickListener(event -> {
            Raum raum;
            if (selectedRoom == null) {
                raum = new Raum();
            } else {
                raum = selectedRoom;
            }
            if (roomBinder.writeBeanIfValid(raum)) {
                if (selectedRoom == null && raumService.refNrExists(raum.getRefNr())) {
                    Notification.show("Die Referenznummer existiert bereits", 2000, Notification.Position.MIDDLE);
                } else {
                    roomService.save(raum);
                    updateGrid();
                    dialog.close();
                }
            } else {
                Notification.show("Bitte überprüfen Sie Ihre Eingaben", 2000, Notification.Position.MIDDLE);
            }
        });

        dialog.getFooter().add(cancelButton, saveButton);

        dialog.open();
    }

    /**
     * Öffnet den Dialog zum Löschen eines Raumes
     */
    private void openDeleteDialog() {
        Optional<Raum> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedRoom.isEmpty()) {
            Notification.show("Bitte wählen Sie einen Raum aus", 2000, Notification.Position.MIDDLE);
        } else if (!buchungService.findAllByRoom(selectedRoom.get()).isEmpty()) {
            Notification errorNotification = new Notification("Es existieren noch Buchungen für diesen Raum", 4000, Notification.Position.MIDDLE);
            errorNotification.getElement().getThemeList().add("error");
            errorNotification.open();
        } else {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("Raum " + selectedRoom.get().getRefNr() + " löschen?");
            confirmDeleteDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden.");

            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmButtonTheme("error primary");

            confirmDeleteDialog.setConfirmButton("Löschen", event -> {
                roomService.delete(selectedRoom.get());
                updateGrid();
                confirmDeleteDialog.close();
            });

            confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());

            confirmDeleteDialog.open();
        }
    }

    /**
     * Öffnet den Dialog zum Buchen eines Raumes
     */
    private void openRoomBookDialog() {
        Optional<Raum> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedRoom.isPresent()) {
            Dialog roomBookDialog = new BuchungAnlegenBearbeitenDialog(null, selectedRoom.get(), null, roomService, dozentService, buchungService, veranstaltungService,
                    currentUser);
            roomBookDialog.open();
        } else {
            Notification.show("Bitte einen Raum auswählen", 4000, Notification.Position.MIDDLE);
        }

    }

    /**
     * Öffnet den Dialog zum Anzeigen der Buchungen eines Raumes
     */
    private void openShowBookingsDialog() {
        Optional<Raum> selectedRoom = roomGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedRoom.isPresent()) {
            Dialog showBookingsDialog = new BuchungenAnzeigenDialog(selectedRoom.get(), roomService, dozentService, buchungService, veranstaltungService, currentUser);
            showBookingsDialog.open();
        } else {
            Notification.show("Bitte einen Raum auswählen", 4000, Notification.Position.MIDDLE);
        }
    }

    private void updateGrid() {
        if (currentUser.get().isPresent()) {
            if (currentUser.get().get().getRoles().contains(Role.ADMIN)) {
                dataView = roomGrid.setItems(roomService.findAll());
                setupFilter();
            } else {
                dataView = roomGrid.setItems(roomService.findAllByFachbereich(currentUser.get().get().getFachbereich()));
                setupFilter();
            }
        }
    }

    /**
     * Statische Filter Klasse für die Räume
     */
    private static class RoomFilter {
        private final GridListDataView<Raum> dataView;

        private String refNr;
        private Fachbereich fachbereich;
        private String position;
        private Raumtyp raumtyp;
        private Set<Ausstattung> ausstattung;
        private int capacity;

        public RoomFilter(GridListDataView<Raum> dataView) {
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

        public boolean test(Raum room) {
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

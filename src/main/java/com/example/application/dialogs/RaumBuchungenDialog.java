package com.example.application.dialogs;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.RaumService;
import com.example.application.services.VeranstaltungService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

@Route(value = "buchungen-raum", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
@PageTitle("Raumbuchungen")
public class RaumBuchungenDialog extends Dialog {

    private final BuchungService buchungService;
    private final Grid<Buchung> raumBuchungGrid = new Grid<>(Buchung.class, false);
    private final RaumService roomService;
    private final DozentService dozentService;
    private final VeranstaltungService veranstaltungService;
    private final Optional<Raum> selectedRoom;
    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    public RaumBuchungenDialog(Optional<Raum> raum, RaumService roomService, DozentService dozentService, BuchungService buchungService, VeranstaltungService veranstaltungService) {
        this.buchungService = buchungService;
        this.roomService = roomService;
        this.selectedRoom = raum;
        this.dozentService = dozentService;
        this.veranstaltungService = veranstaltungService;
        setupButtons();
        setupGrid();
        add(buttonLayout, raumBuchungGrid);
    }

    private void setupButtons() {
        Button editBookingButton = new Button("Buchung bearbeiten", new Icon(VaadinIcon.EDIT));
        editBookingButton.addClickListener(click -> openEditDialog());

        Button deleteBookingButton = new Button("Buchung löschen", new Icon(VaadinIcon.TRASH));
        deleteBookingButton.addClickListener(click -> openDeleteDialog());

        buttonLayout.add(editBookingButton, deleteBookingButton);
    }

    private void setupGrid() {

        GridListDataView<Buchung> dataView = raumBuchungGrid.setItems(buchungService.findAll());
        raumBuchungGrid.addColumn(Buchung::getRoom).setHeader("Raumnummer").setKey("room");
        raumBuchungGrid.addColumn(Buchung::getVeranstaltung).setHeader("Veranstaltung").setKey("veranstaltung");
        raumBuchungGrid.addColumn(Buchung::getDozent).setHeader("Dozent").setKey("dozent");
        raumBuchungGrid.addColumn(Buchung::getDate).setHeader("Datum").setKey("date");
        raumBuchungGrid.addColumn(Buchung::getStartZeit).setHeader("StartZeit").setKey("startZeit");
        raumBuchungGrid.addColumn(Buchung::getEndZeit).setHeader("EndZeit").setKey("endZeit");

        raumBuchungGrid.getColumnByKey("room").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("veranstaltung").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("dozent").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("date").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("startZeit").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("endZeit").setAutoWidth(true).setFlexGrow(0);

        GridSortOrder<Buchung> sortOrderDate = new GridSortOrder<>(raumBuchungGrid.getColumnByKey("date"), SortDirection.ASCENDING);
        GridSortOrder<Buchung> sortOrderTime = new GridSortOrder<>(raumBuchungGrid.getColumnByKey("startZeit"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<Buchung>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrderDate);
        sortOrders.add(sortOrderTime);
        raumBuchungGrid.sort(sortOrders);

        raumBuchungGrid.setMinHeight("80vh");
        raumBuchungGrid.setMinWidth("80vw");

        setupFilter(dataView);
    }

    private void setupFilter(GridListDataView<Buchung> dataView) {
        BuchungFilter buchungFilter = new BuchungFilter(dataView);

        raumBuchungGrid.getHeaderRows().clear();
        HeaderRow headerRow = raumBuchungGrid.appendHeaderRow();

        Consumer<Raum> roomFilterChangeConsumer = buchungFilter::setRoom;
        ComboBox<Raum> roomComboBox = new ComboBox<>();
        roomComboBox.setWidthFull();
        roomComboBox.setItems(roomService.findAll());
        roomComboBox.setClearButtonVisible(true);
        roomComboBox.addValueChangeListener(e -> roomFilterChangeConsumer.accept(e.getValue()));
        if (selectedRoom.isPresent()) {
            roomComboBox.setValue(selectedRoom.get());
            roomComboBox.setEnabled(false);
        }
        headerRow.getCell(raumBuchungGrid.getColumnByKey("room")).setComponent(roomComboBox);

        Consumer<Veranstaltung> veranstaltungFilterChangeConsumer = buchungFilter::setVeranstaltung;
        ComboBox<Veranstaltung> veranstaltungComboBox = new ComboBox<>();
        veranstaltungComboBox.setWidthFull();
        veranstaltungComboBox.setItems(veranstaltungService.findAll());
        veranstaltungComboBox.setClearButtonVisible(true);
        veranstaltungComboBox.addValueChangeListener(e -> veranstaltungFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("veranstaltung")).setComponent(veranstaltungComboBox);

        Consumer<Dozent> dozentFilterChangeConsumer = buchungFilter::setDozent;
        ComboBox<Dozent> dozentComboBox = new ComboBox<>();
        dozentComboBox.setWidthFull();
        dozentComboBox.setItems(dozentService.findAll());
        dozentComboBox.setClearButtonVisible(true);
        dozentComboBox.addValueChangeListener(e -> dozentFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("dozent")).setComponent(dozentComboBox);

        Consumer<LocalDate> dateFilterChangeConsumer = buchungFilter::setDate;
        DatePicker datePicker = new DatePicker();
        datePicker.setWidthFull();
        datePicker.setClearButtonVisible(true);
        datePicker.addValueChangeListener(e -> dateFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("date")).setComponent(datePicker);

        Consumer<LocalTime> startZeitFilterChangeConsumer = buchungFilter::setStartZeit;
        TimePicker startZeitPicker = new TimePicker();
        startZeitPicker.setWidthFull();
        startZeitPicker.setClearButtonVisible(true);
        startZeitPicker.addValueChangeListener(e -> startZeitFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("startZeit")).setComponent(startZeitPicker);

        Consumer<LocalTime> endZeitFilterChangeConsumer = buchungFilter::setEndZeit;
        TimePicker endZeitPicker = new TimePicker();
        endZeitPicker.setWidthFull();
        endZeitPicker.setClearButtonVisible(true);
        endZeitPicker.addValueChangeListener(e -> endZeitFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("endZeit")).setComponent(endZeitPicker);
    }

    private void openEditDialog() {
        Optional<Buchung> selectedBuchung = raumBuchungGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedBuchung.isPresent()) {
            Dialog editBookingDialog = new BuchungAnlegenDialog(selectedBuchung, Optional.empty(), Optional.empty(), Optional.empty(), roomService, dozentService, buchungService, veranstaltungService);
            editBookingDialog.open();
        } else {
            Notification.show("Bitte eine Buchung auswählen", 4000, Notification.Position.MIDDLE);
        }
    }

    private void openDeleteDialog() {
        Optional<Buchung> selectedBooking = raumBuchungGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedBooking.isEmpty()) {
            Notification.show("Bitte wählen Sie eine Buchung aus", 2000, Notification.Position.MIDDLE);
        } else {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("Die ausgewählte Buchung löschen?");
            confirmDeleteDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden");
            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmButtonTheme("error primary");

            confirmDeleteDialog.setConfirmButton("Löschen", event -> {
                buchungService.delete(selectedBooking.get());
                raumBuchungGrid.setItems(buchungService.findAll());
                confirmDeleteDialog.close();
            });

            confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());
            confirmDeleteDialog.open();

        }
    }

    private static class BuchungFilter {
        private final GridListDataView<Buchung> dataView;
        private Raum room;
        private Veranstaltung veranstaltung;
        private Dozent dozent;
        private LocalDate date;
        private LocalTime startZeit;
        private LocalTime endZeit;

        public BuchungFilter(GridListDataView<Buchung> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setRoom(Raum room) {
            this.room = room;
            this.dataView.refreshAll();
        }

        public void setVeranstaltung(Veranstaltung veranstaltung) {
            this.veranstaltung = veranstaltung;
            this.dataView.refreshAll();
        }

        public void setDozent(Dozent dozent) {
            this.dozent = dozent;
            this.dataView.refreshAll();
        }

        public void setDate(LocalDate date) {
            this.date = date;
            this.dataView.refreshAll();
        }

        public void setStartZeit(LocalTime startZeit) {
            this.startZeit = startZeit;
            this.dataView.refreshAll();
        }

        public void setEndZeit(LocalTime endZeit) {
            this.endZeit = endZeit;
            this.dataView.refreshAll();
        }

        public boolean test(Buchung buchung) {
            boolean matchesRoom = true;
            if (room != null) {
                matchesRoom = matches(buchung.getRoom().getRefNr(), room.getRefNr());
            }
            boolean matchesDate = true;
            if (date != null) {
                matchesDate = matches(buchung.getDate().toString(), date.toString());
            }
            boolean matchesVeranstaltung = true;
            if (veranstaltung != null) {
                matchesVeranstaltung = matches(buchung.getVeranstaltung().toString(), veranstaltung.toString());
            }
            boolean matchesDozent = true;
            if (dozent != null) {
                matchesDozent = matches(buchung.getDozent().toString(), dozent.toString());
            }
            boolean matchesStartZeit = true;
            if (startZeit != null) {
                matchesStartZeit = matches(buchung.getStartZeit().toString(), startZeit.toString());
            }
            boolean matchesEndZeit = true;
            if (endZeit != null) {
                matchesEndZeit = matches(buchung.getEndZeit().toString(), endZeit.toString());
            }

            return matchesRoom && matchesDate && matchesVeranstaltung && matchesDozent && matchesStartZeit && matchesEndZeit;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }

}

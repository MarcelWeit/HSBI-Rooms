package com.example.application.dialogs;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Role;
import com.example.application.data.enums.Zeitslot;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.RaumService;
import com.example.application.services.VeranstaltungService;
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
import com.vaadin.flow.data.provider.SortDirection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Dialog um Buchungen anzuzeigen
 *
 * @author Mike Wiebe
 */
public class BuchungenAnzeigenDialog extends Dialog {

    private final BuchungService buchungService;
    private final Grid<Buchung> raumBuchungGrid = new Grid<>(Buchung.class, false);
    private final RaumService roomService;
    private final DozentService dozentService;
    private final VeranstaltungService veranstaltungService;
    private final Raum selectedRoom; //canbenull
    private final HorizontalLayout buttonLayout = new HorizontalLayout();
    private final AuthenticatedUser currentUser;

    public BuchungenAnzeigenDialog(Raum raum, RaumService roomService, DozentService dozentService, BuchungService buchungService, VeranstaltungService veranstaltungService, AuthenticatedUser currentUser) {
        this.buchungService = buchungService;
        this.roomService = roomService;
        this.selectedRoom = raum;
        this.dozentService = dozentService;
        this.veranstaltungService = veranstaltungService;
        this.currentUser = currentUser;
        setupButtons();
        setupGrid();
        add(buttonLayout, raumBuchungGrid);
    }

    /**
     * Methode um die Buttons zum Bearbeiten und Löschen von Buchungen hinzuzufügen
     *
     * @author Mike Wiebe
     */
    private void setupButtons() {
        Button editBookingButton = new Button("Buchung bearbeiten", new Icon(VaadinIcon.EDIT));
        editBookingButton.addClickListener(click -> openEditDialog());

        Button deleteBookingButton = new Button("Buchung löschen", new Icon(VaadinIcon.TRASH));
        deleteBookingButton.addClickListener(click -> openDeleteDialog());
        deleteBookingButton.setId("button-deletebooking");

        buttonLayout.add(editBookingButton, deleteBookingButton);
    }

    /**
     * Methode um das Grid zu erstellen um darin Buchungen anzuzeigen
     *
     * @author Mike Wiebe
     */
    private void setupGrid() {

        GridListDataView<Buchung> dataView = raumBuchungGrid.setItems(buchungService.findAll());
        raumBuchungGrid.addColumn(Buchung::getRoom).setHeader("Raumnummer").setKey("room");
        raumBuchungGrid.addColumn(Buchung::getVeranstaltung).setHeader("Veranstaltung").setKey("veranstaltung");
        raumBuchungGrid.addColumn(Buchung::getDozent).setHeader("Dozent").setKey("dozent");
        raumBuchungGrid.addColumn(Buchung::getDate).setHeader("Datum").setKey("date");
        raumBuchungGrid.addColumn(Buchung::getZeitslot).setHeader("Zeitslot").setKey("zeitslot");

        raumBuchungGrid.getColumnByKey("room").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        raumBuchungGrid.getColumnByKey("veranstaltung").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        raumBuchungGrid.getColumnByKey("dozent").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        raumBuchungGrid.getColumnByKey("date").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        raumBuchungGrid.getColumnByKey("zeitslot").setAutoWidth(true).setFlexGrow(0).setSortable(true);

        GridSortOrder<Buchung> sortOrderRoom = new GridSortOrder<>(raumBuchungGrid.getColumnByKey("room"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<Buchung>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrderRoom);
        raumBuchungGrid.sort(sortOrders);

        raumBuchungGrid.setMinHeight("80vh");
        raumBuchungGrid.setMinWidth("80vw");

        setupFilter(dataView);
    }

    /**
     * Methode um die Filter zu den einzelnen Attributen der Buchung in dem Grid zu erstellen
     *
     * @param dataView
     *
     * @author Mike Wiebe
     */
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
        if (selectedRoom != null) {
            roomComboBox.setValue(selectedRoom);
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
        //Wenn der Nutzer ein Dozent ist, soll dieser nur die Buchungen einsehen können, in denen er selbst als Dozent eingetragen ist
        if (currentUser.get().isPresent()) {
            if (currentUser.get().get().getRoles().contains(Role.DOZENT)) {
                Optional<Dozent> optionalDozent = dozentService.findByVornameAndNachname(
                        currentUser.get().get().getFirstName(),
                        currentUser.get().get().getLastName()
                );
                if (optionalDozent.isPresent()) {
                    Dozent dozentFound = optionalDozent.get();
                    dozentComboBox.setItems(List.of(dozentFound));
                    dozentComboBox.setValue(dozentFound);
                    dozentComboBox.setEnabled(false);
                }
            }
        }
        headerRow.getCell(raumBuchungGrid.getColumnByKey("dozent")).setComponent(dozentComboBox);

        Consumer<LocalDate> dateFilterChangeConsumer = buchungFilter::setDate;
        DatePicker datePicker = new DatePicker();
        datePicker.setWidthFull();
        datePicker.setClearButtonVisible(true);
        datePicker.addValueChangeListener(e -> dateFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("date")).setComponent(datePicker);

        Consumer<Zeitslot> zeitslotFilterChangeConsumer = buchungFilter::setZeitslot;
        ComboBox<Zeitslot> zeitslotComboBox = new ComboBox<>();
        zeitslotComboBox.setWidthFull();
        zeitslotComboBox.setClearButtonVisible(true);
        zeitslotComboBox.addValueChangeListener(e -> zeitslotFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("zeitslot")).setComponent(zeitslotComboBox);
    }

    /**
     * Methode um die Eingabemaske zum editieren einer Buchung zu öffnen
     *
     * @author Mike Wiebe
     */
    private void openEditDialog() {
        Optional<Buchung> selectedBuchung = raumBuchungGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedBuchung.isPresent()) {
            BuchungAnlegenBearbeitenDialog editBookingDialog = new BuchungAnlegenBearbeitenDialog(selectedBuchung.get(), null, null, roomService,
                    dozentService,
                    buchungService, veranstaltungService, currentUser);
            editBookingDialog.open();
            this.close();
        } else {
            Notification.show("Bitte eine Buchung auswählen", 4000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Methode um die Eingabemaske zum löschen einer Buchung zu öffnen
     *
     * @author Mike Wiebe
     */
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
                confirmDeleteDialog.close();
                this.close();
            });

            confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());
            confirmDeleteDialog.open();

        }
    }

    /**
     * Klasse um die einzelnen Filter zu implementieren um Buchungen zu filtern
     *
     * @author Mike Wiebe
     */
    private static class BuchungFilter {
        private final GridListDataView<Buchung> dataView;
        private Raum room;
        private Veranstaltung veranstaltung;
        private Dozent dozent;
        private LocalDate date;
        private Zeitslot zeitslot;

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

        public void setZeitslot(Zeitslot zeitslot) {
            this.zeitslot = zeitslot;
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
            boolean matchesZeitslot = true;
            if (zeitslot != null) {
                matchesZeitslot = matches(buchung.getZeitslot().toString(), zeitslot.toString());
            }

            return matchesRoom && matchesDate && matchesVeranstaltung && matchesDozent && matchesZeitslot;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }

}

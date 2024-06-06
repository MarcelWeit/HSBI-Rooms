package com.example.application.views;

import com.example.application.data.entities.*;
import com.example.application.data.enums.Zeitslot;
import com.example.application.dialogs.BuchungAnlegenBearbeitenDialog;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;


@Route(value = "meine-buchungen", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
@PageTitle("MeineBuchungen")
public class MeineBuchungenView extends VerticalLayout {

    private final BuchungService buchungService;
    private final UserService userService;
    private final DozentService dozentService;
    private final RaumService raumService;
    private final VeranstaltungService veranstaltungService;

    private final AuthenticatedUser currentUser;

    private Grid<Buchung> grid;

    public MeineBuchungenView(BuchungService buchungService, UserService userService, DozentService dozentService, AuthenticatedUser currentUser, RaumService raumService, VeranstaltungService veranstaltungService) {
        this.buchungService = buchungService;
        this.userService = userService;
        this.dozentService = dozentService;
        this.currentUser = currentUser;
        this.raumService = raumService;
        this.veranstaltungService = veranstaltungService;

        setupGrid();
        add(grid);

    }
    private void setupGrid() {
        grid = new Grid<>();

        grid.addColumn(Buchung::getRoom).setHeader("Raum").setKey("raum");
        grid.addColumn(Buchung::getVeranstaltung).setHeader("Veranstaltung").setKey("veranstaltung");
        grid.addColumn(Buchung::getDate).setHeader("Datum").setKey("datum");
        grid.addColumn(Buchung::getZeitslot).setHeader("Zeitslot").setKey("zeitslot");

        grid.setMinHeight("80vh");

        refreshGrid();
        setupButtons();
        setupFilters();

    }
    private void refreshGrid() {
        User userData = currentUser.get().get();

        Optional<Dozent> dozent = dozentService.findByVornameAndNachname(userData.getFirstName(), userData.getLastName());

        Set<Buchung> dozentBuchungen = buchungService.findAllByDozent(dozent.get());

        Set<Buchung> userBuchungen = buchungService.findAllByUser(userData);

        Set<Buchung> allBuchungen = new HashSet<>();

        if(dozentBuchungen != null) {
            allBuchungen.addAll(dozentBuchungen);
        }

        if(userBuchungen != null) {
            userBuchungen.forEach(buchung -> {
                if(allBuchungen.contains(buchung)) {
                } else {
                    allBuchungen.add(buchung);
                }
            });
        }

        Set<Buchung> testSet = buchungService.findAllByUserOrDozent(userData, dozent.get());

        grid.setItems(testSet);
    }
    private void setupFilters() {
        BuchungFilter bFilter = new BuchungFilter(grid.getListDataView());

        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

        Consumer<Set<Raum>> raumFilterChangeConsumer = bFilter::setRaum;
        MultiSelectComboBox<Raum> raumComboBox = new MultiSelectComboBox<>();
        raumComboBox.setItems(raumService.findAll());
        raumComboBox.addValueChangeListener(e -> raumFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("raum")).setComponent(raumComboBox);

        Consumer<Set<Veranstaltung>> veranstaltungFilterChangeConsumer = bFilter::setVeranstaltung;
        MultiSelectComboBox<Veranstaltung> veranstaltungComboBox = new MultiSelectComboBox<>();
        veranstaltungComboBox.setItems(veranstaltungService.findAll());
        veranstaltungComboBox.addValueChangeListener(e -> veranstaltungFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("veranstaltung")).setComponent(veranstaltungComboBox);

        Consumer<Set<Zeitslot>> zeitslotFilterChangeConsumer = bFilter::setZeitslot;
        MultiSelectComboBox<Zeitslot> zeitslotComboBox = new MultiSelectComboBox<>();
        zeitslotComboBox.setItems(Zeitslot.values());
        zeitslotComboBox.addValueChangeListener(e -> zeitslotFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("zeitslot")).setComponent(zeitslotComboBox);

        Consumer<LocalDate> datumFilterChangeConsumer = bFilter::setDate;
        DatePicker datePicker = new DatePicker();
        datePicker.addValueChangeListener(e -> datumFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("datum")).setComponent(datePicker);
    }
    private void setupButtons() {
        Button buchungBearbeiten = new Button("Buchung bearbeiten", new Icon(VaadinIcon.EDIT));
        buchungBearbeiten.addClickListener(e -> {
            Optional<Buchung> selectedBuchung = grid.getSelectionModel().getFirstSelectedItem();
            if (selectedBuchung.isEmpty()) {
                Notification.show("Bitte wählen Sie eine Buchung aus", 2000, Notification.Position.MIDDLE);
            } else {
                Dialog buchungEditDialog = new BuchungAnlegenBearbeitenDialog(selectedBuchung.get(), Optional.empty(), Optional.empty(), raumService, dozentService, buchungService, veranstaltungService,
                        currentUser);
                buchungEditDialog.open();
                buchungEditDialog.addDialogCloseActionListener(event -> refreshGrid());
            }
        });

        Button buchungLoeschen = new Button("Buchung löschen", new Icon(VaadinIcon.TRASH));
        buchungLoeschen.addClickListener(e -> openDeleteDialog());

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(buchungBearbeiten, buchungLoeschen);

        add(layout);
    }
    private void openDeleteDialog() {
        Optional<Buchung> selectedBuchung = grid.getSelectionModel().getFirstSelectedItem();
        if(selectedBuchung.isEmpty()) {
            Notification.show("Bitte wählen Sie eine Buchung aus", 2000, Notification.Position.MIDDLE);
        } else {
            Buchung current = selectedBuchung.get();
            ConfirmDialog deleteConfirmDialog = new ConfirmDialog();

            deleteConfirmDialog.setHeader("Buchung für " + current.getRoom().toString() +
                    " am " + current.getDate().toString() + " um " + current.getZeitslot().toString() + " löschen?");
            deleteConfirmDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden.");

            deleteConfirmDialog.setCancelable(true);
            deleteConfirmDialog.setConfirmButtonTheme("error primary");

            deleteConfirmDialog.setConfirmButton("Löschen", e -> {
                buchungService.delete(current);
                refreshGrid();
                deleteConfirmDialog.close();
            });

            deleteConfirmDialog.setCancelButton("Abbrechen", e -> deleteConfirmDialog.close());

            deleteConfirmDialog.open();
        }
    }
    private static class BuchungFilter {
        private final GridListDataView<Buchung> dataView;

        private Set<Raum> raum;
        private Set<Veranstaltung> veranstaltung;
        private LocalDate date;
        private Set<Zeitslot> zeitslot;

        public BuchungFilter(GridListDataView<Buchung> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::createFilter);
        }

        public void setRaum(Set<Raum> raum) {
            this.raum = raum;
            this.dataView.refreshAll();
        }
        public void setVeranstaltung(Set<Veranstaltung> veranstaltung) {
            this.veranstaltung = veranstaltung;
            this.dataView.refreshAll();
        }
        public void setDate(LocalDate date) {
            this.date = date;
            this.dataView.refreshAll();
        }
        public void setZeitslot(Set<Zeitslot> zeitslot) {
            this.zeitslot = zeitslot;
            this.dataView.refreshAll();
        }
        public boolean createFilter(Buchung b) {
            boolean matchesRaum = true;
            boolean matchesVeranstaltung = true;
            boolean matchesDate = true;
            boolean matchesZeitslot = true;

            matchesRaum = compareSet(b.getRoom().toString(), raum);
            if(date != null) {
                matchesDate = date.equals(b.getDate());
            }
            matchesZeitslot = compareSet(b.getZeitslot().toString(), zeitslot);
            matchesVeranstaltung = compareSet(b.getVeranstaltung().toString(), veranstaltung);

            return matchesRaum && matchesVeranstaltung && matchesDate && matchesZeitslot;
        }
        private boolean compareSet(String value, Set<?> searchTerm) {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return true;
            }

            Iterator<?> iter = searchTerm.iterator();
            boolean result = false;
            while (iter.hasNext()) {
                if (value.equalsIgnoreCase(iter.next().toString())) {
                    result = true;
                }
            }
            return result;
        }
    }

}
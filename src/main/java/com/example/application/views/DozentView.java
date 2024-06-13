package com.example.application.views;

import com.example.application.comparator.NachnameComparator;
import com.example.application.data.entities.Dozent;
import com.example.application.data.enums.Anrede;
import com.example.application.data.enums.Fachbereich;
import com.example.application.services.DozentService;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Route(value = "dozent-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPLANUNG"})
@RolesAllowed({"ADMIN", "FBPLANUNG"})
@Uses(Icon.class)
@PageTitle("Dozenten")
public class DozentView extends VerticalLayout {
    private final DozentService dozentService;
    private final VeranstaltungService veranstaltungService;
    private final Grid<Dozent> dozentGrid = new Grid<>(Dozent.class, false);
    private final Binder<Dozent> binder = new Binder<>(Dozent.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();
    private GridListDataView<Dozent> dataView;
    private HeaderRow headerRow;


    public DozentView(DozentService dozentService, VeranstaltungService veranstaltungService) {
        this.dozentService = dozentService;
        this.veranstaltungService = veranstaltungService;

        //Grid/Buttons einrichten
        setupGrid();
        setupButtons();

        add(buttonLayout, dozentGrid);
    }

    // Methode zur Einrichtung des Grids
    private void setupGrid() {
        // Datenansicht des Grids mit allen vorhandenen Dozenten befüllen
        dataView = dozentGrid.setItems(dozentService.findAll());

        // Spalten für die Attribute der Dozenten hinzufügen und konfigurieren
        dozentGrid.addColumn(Dozent::getAnrede).setHeader("Anrede").setKey("anrede");
        dozentGrid.addColumn(Dozent::getNachname).setHeader("Nachname")
                .setComparator(new NachnameComparator())
                .setKey("nachname");
        dozentGrid.addColumn(Dozent::getVorname).setHeader("Vorname").setKey("vorname");
        dozentGrid.addColumn(Dozent::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        dozentGrid.addColumn(Dozent::getAkad_titel).setHeader("Akademischer Titel").setKey("akad_titel");
        dozentGrid.setColumnReorderingAllowed(true);
        dozentGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        dozentGrid.getColumns().forEach(column -> column.setAutoWidth(true));

        // Filter für das Grid einrichten
        setupFilter();

        //Sortierreihenfolge standartmäßig auf Nachname aufsteigend gesetzt
        GridSortOrder<Dozent> sortOrderNachname = new GridSortOrder<>(dozentGrid.getColumnByKey("nachname"), SortDirection.ASCENDING);
        List<GridSortOrder<Dozent>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrderNachname);
        dozentGrid.sort(sortOrders);
        // Mindesthöhe des Grids festlegen, um ein Scrollen zu ermöglichen
        dozentGrid.setMinHeight("80vh");
    }

    // Methode zur Einrichtung der Filterfunktionen für das Grid
    private void setupFilter() {
        // Erstellen einer Instanz von DozentFilter und Verknüpfen mit der Datenansicht
        DozentFilter dozentFilter = new DozentFilter(dataView);

        // Überprüfen, ob die Header-Zeile des Grids bereits vorhanden ist, und falls nicht, eine erstellen
        if (headerRow == null) {
            headerRow = dozentGrid.appendHeaderRow();
        }

        // ComboBox für die Filterung nach Anrede erstellen und konfigurieren
        ComboBox<Anrede> anredeComboBox = new ComboBox<>();
        anredeComboBox.setWidth("200px");
        anredeComboBox.setItems(Anrede.values());
        anredeComboBox.setClearButtonVisible(true);
        anredeComboBox.addValueChangeListener(e -> dozentFilter.setAnrede(e.getValue()));
        headerRow.getCell(dozentGrid.getColumnByKey("anrede")).setComponent(anredeComboBox);

        // Textfelder für die Filterung nach Nachname, Vorname und akademischem Titel erstellen und konfigurieren
        headerRow.getCell(dozentGrid.getColumnByKey("nachname")).setComponent(createStringFilterHeader("Filter", dozentFilter::setNachname));
        headerRow.getCell(dozentGrid.getColumnByKey("vorname")).setComponent(createStringFilterHeader("Filter", dozentFilter::setVorname));
        headerRow.getCell(dozentGrid.getColumnByKey("akad_titel")).setComponent(createStringFilterHeader("Filter", dozentFilter::setTitel));

        // ComboBox für die Filterung nach Fachbereich erstellen und konfigurieren
        ComboBox<Fachbereich> fachbereichComboBox = new ComboBox<>();
        fachbereichComboBox.setWidth("200px");
        fachbereichComboBox.setItems(Fachbereich.values());
        fachbereichComboBox.setClearButtonVisible(true);
        fachbereichComboBox.addValueChangeListener(e -> dozentFilter.setFachbereich(e.getValue()));
        headerRow.getCell(dozentGrid.getColumnByKey("fachbereich")).setComponent(fachbereichComboBox);
    }

    // Methode zur Erstellung eines Textfelds für die Filterung mit einem angegebenen Platzhalter und Filterfunktion
    private static TextField createStringFilterHeader(String placeholder, Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setPlaceholder(placeholder);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        return textField;
    }

    // Methode zur Einrichtung der Buttons für Hinzufügen, Bearbeiten und Löschen von Dozenten
    private void setupButtons() {
        // Schaltfläche zum Hinzufügen eines Dozenten erstellen und konfigurieren
        Button addDozentButton = new Button("Dozent hinzufügen", new Icon(VaadinIcon.PLUS));
        addDozentButton.addClickListener(e -> openCreateEditDialog(Optional.empty()));

        //Button zum Bearbeiten eines ausgewählten Dozenten erstellen und konfigurieren
        Button editDozentButton = new Button("Dozent bearbeiten", new Icon(VaadinIcon.EDIT));
        editDozentButton.addClickListener(e -> {
            // Den ausgewählten Dozenten aus dem Grid abrufen
            Optional<Dozent> selectedDozent = dozentGrid.getSelectionModel().getFirstSelectedItem();
            // Überprüfen, ob ein Dozent ausgewählt wurde
            if (selectedDozent.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Dozenten aus", 2000, Notification.Position.MIDDLE);
            } else {
                // Wenn ein Dozent ausgewählt wurde, den Bearbeitungsdialog öffnen und den ausgewählten Dozenten übergeben
                openCreateEditDialog(selectedDozent);
            }
        });

        // Button zum Löschen eines ausgewählten Dozenten erstellen und konfigurieren
        Button deleteDozentButton = new Button("Dozent löschen", new Icon(VaadinIcon.TRASH));
        deleteDozentButton.addClickListener(e -> {
            // Den ausgewählten Dozenten aus dem Grid abrufen
            Optional<Dozent> selectedDozent = dozentGrid.getSelectionModel().getFirstSelectedItem();
            // Überprüfen, ob ein Dozent ausgewählt wurde
            if (selectedDozent.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Dozenten aus", 2000, Notification.Position.MIDDLE);
            } else {
                // Wenn ein Dozent ausgewählt wurde, den Löschdialog öffnen und den ausgewählten Dozenten übergeben
                openDeleteDialog(selectedDozent.get());
            }
        });
        // Die Buttons zur horizontalen Layout hinzufügen
        buttonLayout.add(addDozentButton, editDozentButton, deleteDozentButton);
    }

    // Methode zur Öffnung des Dialogs zum Hinzufügen oder Bearbeiten eines Dozenten
    private void openCreateEditDialog(Optional<Dozent> selectedDozent) {
        boolean inEditDialog = false;
        Dozent dozent;
        String fehlermeldung;

        // Überprüfen, ob ein Dozent ausgewählt wurde
        if (selectedDozent.isPresent()) {
            // Dozent wurde ausgewählt also wurde der Edit-Button geklickt
            inEditDialog = true;
            dozent = selectedDozent.get();
            fehlermeldung = "Eintrag erfolgreich aktualisiert.";
        } else {
            // Dozent wurde nicht ausgewählt also wurde der Hinzufügen-Button geklickt
            dozent = new Dozent();
            fehlermeldung = "Eintrag erfolgreich gespeichert.";
        }

        // Dialog erstellen und Größe festlegen
        Dialog dialog = new Dialog();
        dialog.setMaxWidth("25vw");
        dialog.setMinWidth("200px");

        // Formularlayout
        FormLayout form = new FormLayout();

        // Textfelder/Comboboxen für Anrede, Nachname, Vorname, Fachbereich und akademischen Titel erstellen und konfigurieren
        ComboBox<Anrede> anrede = new ComboBox<>("Anrede");
        anrede.setItems(Anrede.values());
        anrede.setItemLabelGenerator(Anrede::toString);
        anrede.setRequired(true);

        TextField nachname = new TextField("Nachname");
        TextField vorname = new TextField("Vorname");

        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);
        fachbereich.setRequired(true);

        TextField akad_titel = new TextField("Akademischer Titel");
        form.add(anrede, nachname, vorname, fachbereich, akad_titel);
        dialog.add(form);

        // Die Felder des Binders mit den entsprechenden Entitätsattributen verknüpfen
        binder.forField(anrede).asRequired("Bitte eine Anrede auswählen.").bind(Dozent::getAnrede, Dozent::setAnrede);
        binder.forField(nachname).asRequired("Bitte Nachname angeben.").bind(Dozent::getNachname, Dozent::setNachname);
        binder.forField(vorname).asRequired("Bitte Vorname angeben.").bind(Dozent::getVorname, Dozent::setVorname);
        binder.forField(fachbereich).asRequired("Bitte einen Fachbereich auswählen.").bind(Dozent::getFachbereich, Dozent::setFachbereich);
        binder.forField(akad_titel).asRequired("Bitte einen Akademischen Titel angeben.").bind(Dozent::getAkad_titel, Dozent::setAkad_titel);

        // Falls im Bearbeitungsmodus, die Werte der Felder mit den Daten des ausgewählten Dozenten füllen
        if (inEditDialog) {
            binder.readBean(selectedDozent.get());
        }

        // Abbrechen-Button erstellen
        Button cancelButton = new Button("Abbrechen", event -> {
            binder.removeBean();
            dialog.close();
        });

        // Speichern-Button erstellen
        Button saveButton = new Button("Speichern");
        saveButton.addClickListener(event -> {
            // Überprüfen, ob die eingegebenen Daten gültig sind und in die Entität übertragen werden können
            if (binder.writeBeanIfValid(dozent)) {
                Optional<Dozent> existingDozenten = dozentService.findByVornameAndNachname(dozent.getVorname(), dozent.getNachname());
                boolean isDuplicate = existingDozenten.stream().anyMatch(d -> d.getId() != dozent.getId());
                if (!isDuplicate) {
                    // Wenn kein Duplikat gefunden wurde, den Dozenten speichern, Grid aktualisieren, Dialog schließen und Benachrichtigung anzeigen
                    dozentService.save(dozent);
                    refreshGrid();
                    dialog.close();
                    Notification.show(fehlermeldung, 3000, Notification.Position.BOTTOM_CENTER);
                } else {
                    Notification.show("Ein Dozent mit diesem Vor- und Nachnamen existiert bereits.", 3000, Notification.Position.BOTTOM_CENTER);
                }
            }
        });
        // Buttons dem Dialog hinzufügen und Dialog öffnen
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(Dozent selectedDozent) {

        //Stellt sicher, dass ein Dozent der sich in einer Veranstaltung befindet, nicht gelöscht werden kann
        if (!veranstaltungService.findVeranstaltungSet(selectedDozent).isEmpty()) {
            Notification.show("Dieser Dozent befindet sich in einer Veranstaltung und kann daher nicht gelöscht werden.", 3000, Notification.Position.MIDDLE);
            return;
        }

        // Löschdialog erstellen und konfigurieren
        ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
        confirmDeleteDialog.setHeader("Dozent " + selectedDozent.getNachname() + " löschen?");
        confirmDeleteDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden.");
        confirmDeleteDialog.setCancelable(true);
        confirmDeleteDialog.setConfirmButtonTheme("error primary");

        // Bestätigungsschaltfläche konfigurieren und Aktion festlegen
        confirmDeleteDialog.setConfirmButton("Löschen", event -> {
            dozentService.delete(selectedDozent);   // Den ausgewählten Dozenten löschen
            refreshGrid();                          //Grid aktualisieren
            confirmDeleteDialog.close();            // Den Löschdialog schließen
        });
        // Abbruchschaltfläche
        confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());
        confirmDeleteDialog.open();
    }
    //Grid aktualisieren indem die dataview neu gesetzt wird und die Filter neu eingerichtet werden
    private void refreshGrid() {
        dataView = dozentGrid.setItems(dozentService.findAll());
        setupFilter();
    }

    // Diese Klasse wird verwendet, um die Filterung von Dozenten im Grid zu verwalten
    private static class DozentFilter {
        private final GridListDataView<Dozent> dataView;
        private Anrede anrede;
        private String nachname;
        private String vorname;
        private Fachbereich fachbereich;
        private String akad_titel;

        public DozentFilter(GridListDataView<Dozent> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setAnrede(Anrede anrede) {
            this.anrede = anrede;
            this.dataView.refreshAll();
        }

        public void setNachname(String nachname) {
            this.nachname = nachname;
            this.dataView.refreshAll();
        }

        public void setVorname(String vorname) {
            this.vorname = vorname;
            this.dataView.refreshAll();
        }

        public void setFachbereich(Fachbereich fachbereich) {
            this.fachbereich = fachbereich;
            this.dataView.refreshAll();
        }

        public void setTitel(String titel) {
            this.akad_titel = titel;
            this.dataView.refreshAll();
        }

        // Filterfunktion, um zu überprüfen, ob ein Dozent die Filterkriterien erfüllt
        public boolean test(Dozent dozent) {
            boolean matchesAnrede = anrede == null || dozent.getAnrede() == anrede;
            boolean matchesNachname = matches(dozent.getNachname(), nachname);
            boolean matchesVorname = matches(dozent.getVorname(), vorname);
            boolean matchesFachbereich = fachbereich == null || dozent.getFachbereich() == fachbereich;
            boolean matchesAkadTitel = matches(dozent.getAkad_titel(), akad_titel);
            return  matchesAnrede && matchesNachname && matchesVorname && matchesFachbereich && matchesAkadTitel;
        }

        // Hilfsmethode zum Überprüfen, ob ein Wert mit einem Suchbegriff übereinstimmt
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}
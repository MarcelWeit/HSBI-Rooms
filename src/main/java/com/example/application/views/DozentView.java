package com.example.application.views;

import com.example.application.comparator.NachnameComparator;
import com.example.application.comparator.VornameComparator;
import com.example.application.data.entities.Dozent;
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

        setupGrid();
        setupButtons();

        add(buttonLayout, dozentGrid);
    }

    private void setupGrid() {
        dataView = dozentGrid.setItems(dozentService.findAll());

        dozentGrid.addColumn(Dozent::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        dozentGrid.addColumn(Dozent::getNachname).setHeader("Nachname")
                .setComparator(new NachnameComparator())
                .setKey("nachname");
        dozentGrid.addColumn(Dozent::getVorname).setHeader("Vorname")
                .setComparator(new VornameComparator())
                .setKey("vorname");
        dozentGrid.setColumnReorderingAllowed(true);
        dozentGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        dozentGrid.getColumns().forEach(column -> column.setAutoWidth(true));

        setupFilter();

        GridSortOrder<Dozent> sortOrderNachname = new GridSortOrder<>(dozentGrid.getColumnByKey("nachname"), SortDirection.ASCENDING);
        GridSortOrder<Dozent> sortOrderVorname = new GridSortOrder<>(dozentGrid.getColumnByKey("vorname"), SortDirection.ASCENDING);
        List<GridSortOrder<Dozent>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrderNachname);
        sortOrders.add(sortOrderVorname);

        dozentGrid.sort(sortOrders);
        dozentGrid.setMinHeight("80vh");
    }

    private void setupFilter() {
        DozentFilter dozentFilter = new DozentFilter(dataView);

        if (headerRow == null) {
            headerRow = dozentGrid.appendHeaderRow();
        }

        headerRow.getCell(dozentGrid.getColumnByKey("nachname")).setComponent(createStringFilterHeader("Filter", dozentFilter::setNachname));
        headerRow.getCell(dozentGrid.getColumnByKey("vorname")).setComponent(createStringFilterHeader("Filter", dozentFilter::setVorname));

        ComboBox<Fachbereich> fachbereichComboBox = new ComboBox<>();
        fachbereichComboBox.setWidth("200px");
        fachbereichComboBox.setItems(Fachbereich.values());
        fachbereichComboBox.setClearButtonVisible(true);
        fachbereichComboBox.addValueChangeListener(e -> dozentFilter.setFachbereich(e.getValue()));

        headerRow.getCell(dozentGrid.getColumnByKey("fachbereich")).setComponent(fachbereichComboBox);

    }
    private static TextField createStringFilterHeader(String placeholder, Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setPlaceholder(placeholder);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        return textField;
    }

    private void setupButtons() {
        Button addDozentButton = new Button("Dozent hinzufügen", new Icon(VaadinIcon.PLUS));
        addDozentButton.addClickListener(e -> openCreateEditDialog(Optional.empty()));

        Button editDozentButton = new Button("Dozent bearbeiten", new Icon(VaadinIcon.EDIT));
        editDozentButton.addClickListener(e -> {
            Optional<Dozent> selectedDozent = dozentGrid.getSelectionModel().getFirstSelectedItem();
            if (selectedDozent.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Dozenten aus", 2000, Notification.Position.MIDDLE);
            } else {
                openCreateEditDialog(selectedDozent);
            }
        });

        Button deleteDozentButton = new Button("Dozent löschen", new Icon(VaadinIcon.TRASH));
        deleteDozentButton.addClickListener(e -> {
            Optional<Dozent> selectedDozent = dozentGrid.getSelectionModel().getFirstSelectedItem();
            if (selectedDozent.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Dozenten aus", 2000, Notification.Position.MIDDLE);
            } else {
                openDeleteDialog(selectedDozent.get());
            }
        });

        buttonLayout.add(addDozentButton, editDozentButton, deleteDozentButton);
    }


    private void openCreateEditDialog(Optional<Dozent> selectedDozent) {
        boolean inEditDialog = false;
        Dozent dozent;
        String fehlermeldung;

        if (selectedDozent.isPresent()){
            inEditDialog = true;
            dozent = selectedDozent.get();
            fehlermeldung = "Eintrag erfolgreich aktualisiert.";
        }else{
            dozent = new Dozent();
            fehlermeldung = "Eintrag erfolgreich gespeichert.";
        }

        Dialog dialog = new Dialog();
        dialog.setMaxWidth("25vw");
        dialog.setMinWidth("200px");

        FormLayout form = new FormLayout();
        TextField nachname = new TextField("Nachname");
        TextField vorname = new TextField("Vorname");
        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);
        fachbereich.setRequired(true);
        form.add(nachname, vorname, fachbereich);
        dialog.add(form);

        binder.forField(nachname).asRequired("Bitte Nachname angeben")
                .bind(Dozent::getNachname, Dozent::setNachname);
        binder.forField(vorname).asRequired("Bitte Vorname angeben")
                .bind(Dozent::getVorname, Dozent::setVorname);
        binder.forField(fachbereich).asRequired("Bitte einen Fachbereich auswählen")
                .bind(Dozent::getFachbereich, Dozent::setFachbereich);

        if (inEditDialog){
            binder.readBean(selectedDozent.get());
        }

        Button cancelButton = new Button("Abbrechen", event -> {
            binder.removeBean();
            dialog.close();
        });

        Button saveButton = new Button("Speichern");
        saveButton.addClickListener(event -> {

            if (binder.writeBeanIfValid(dozent)) {
                List<Dozent> existingDozenten = dozentService.findByVornameAndNachname(dozent.getVorname(), dozent.getNachname());
                boolean isDuplicate = existingDozenten.stream().anyMatch(d -> d.getId() != dozent.getId());
                if (!isDuplicate) {
                    dozentService.save(dozent);
                    refreshGrid();
                    dialog.close();
                    Notification.show(fehlermeldung, 3000, Notification.Position.BOTTOM_CENTER);
                } else {
                    Notification.show("Ein Dozent mit diesem Vor- und Nachnamen existiert bereits.", 3000, Notification.Position.BOTTOM_CENTER);
                }
            }
        });

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();

    }

    private void openDeleteDialog(Dozent selectedDozent) {

        if (!veranstaltungService.findVeranstaltungSet(selectedDozent).isEmpty()) {
            Notification.show("Dieser Dozent befindet sich in einer Veranstaltung und kann daher nicht gelöscht werden.", 3000, Notification.Position.MIDDLE);
            return;
        }

        ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
        confirmDeleteDialog.setHeader("Dozent " + selectedDozent.getNachname() + " löschen?");
        confirmDeleteDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden.");
        confirmDeleteDialog.setCancelable(true);
        confirmDeleteDialog.setConfirmButtonTheme("error primary");

        confirmDeleteDialog.setConfirmButton("Löschen", event -> {
            dozentService.delete(selectedDozent);
            refreshGrid();
            confirmDeleteDialog.close();
        });

        confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());
        confirmDeleteDialog.open();
    }

    private void refreshGrid() {
        dataView = dozentGrid.setItems(dozentService.findAll());
        setupFilter();
    }


    private static class DozentFilter {
        private final GridListDataView<Dozent> dataView;
        private String nachname;
        private String vorname;
        private Fachbereich fachbereich;

        public DozentFilter(GridListDataView<Dozent> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
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

        public boolean test(Dozent dozent) {
            boolean matchesNachname = matches(dozent.getNachname(), nachname);
            boolean matchesVorname = matches(dozent.getVorname(), vorname);
            boolean matchesFachbereich = fachbereich == null || dozent.getFachbereich() == fachbereich;

            return matchesNachname && matchesVorname && matchesFachbereich;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}
package com.example.application.views;

import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Fachbereich;
import com.example.application.services.DozentService;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Route(value = "veranstaltungVerwaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung"})
@RolesAllowed({"ADMIN", "FBPlanung"})
@Uses(Icon.class)
@PageTitle("Veranstaltungen")
public class VeranstaltungVerwaltungView extends VerticalLayout {

    private final VeranstaltungService veranstaltungService;
    private final DozentService dozentService;

    private final Grid<Veranstaltung> grid = new Grid<>(Veranstaltung.class, false);

    private final HorizontalLayout buttonLayout = new HorizontalLayout();
    private GridListDataView<Veranstaltung> gridDataView;
    private HeaderRow headerRow;

    //private final Crud<Veranstaltung> crud;

    public VeranstaltungVerwaltungView(VeranstaltungService veranstaltungService, DozentService dozentService) {
        this.veranstaltungService = veranstaltungService;
        this.dozentService = dozentService;

        //crud = new Crud<>(Veranstaltung.class, createEditor());

        setupButtons();
        setupGrid();

        add(buttonLayout);
        add(grid);

    }

    private static Component createStringFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }

    /*
    private CrudEditor<Veranstaltung> createEditor() {

        TextField veranstaltung = new TextField("VeranstaltungsID");
        TextField bezeichnung = new TextField("Bezeichnung");

        ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
        dozent.setItems(dozentService.findAll());
        dozent.setItemLabelGenerator(Dozent::getNachname);

        IntegerField teiln = new IntegerField("Teilnehmerzahl");

        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);

        FormLayout form = new FormLayout(veranstaltung, bezeichnung, dozent, teiln, fachbereich);

        Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);
        binder.forField(veranstaltung).asRequired().bind(Veranstaltung::getId, Veranstaltung::setId);
        binder.forField(bezeichnung).asRequired().bind(Veranstaltung::getBezeichnung, Veranstaltung::setBezeichnung);
        binder.forField(dozent).asRequired().bind(Veranstaltung::getDozent, Veranstaltung::setDozent);
        binder.forField(teiln).asRequired().bind(Veranstaltung::getTeilnehmerzahl, Veranstaltung::setTeilnehmerzahl);
        binder.forField(fachbereich).asRequired().bind(Veranstaltung::getFachbereich, Veranstaltung::setFachbereich);

        return new BinderCrudEditor<>(binder, form);

    }

    private void setupGrid() {
        Grid<Veranstaltung> grid = crud.getGrid();

        grid.getColumnByKey("vaadin-crud-edit-column").setFrozenToEnd(true);

        grid.setColumnOrder(
                grid.getColumnByKey("veranstaltung"),
                grid.getColumnByKey("bezeichnung"),
                grid.getColumnByKey("dozent"),
                grid.getColumnByKey("teilnehmerzahl"),
                grid.getColumnByKey("fachbereich"),
                grid.getColumnByKey("vaadin-crud-edit-column"));
    }

    private void setupDataProvider() {
        VeranstaltungDataProvider dataProvider = new VeranstaltungDataProvider(veranstaltungService);
        crud.setDataProvider(dataProvider);

        crud.addDeleteListener(deleteEvent -> {
            dataProvider.deleteVeranstaltung(deleteEvent.getItem());
            dataProvider.refreshAll();
        });
        crud.addSaveListener(saveEvent -> {
            dataProvider.saveVeranstaltung(saveEvent.getItem());
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

     */
    private void setupButtons() {
        Button create = new Button("Veranstaltung anlegen", new Icon(VaadinIcon.PLUS));
        create.addClickListener(e -> createDialog(Optional.empty()));

        Button edit = new Button("Veranstaltung bearbeiten", new Icon(VaadinIcon.EDIT));
        edit.addClickListener(e -> checkEditDialog());

        Button delete = new Button("Veranstaltung löschen", new Icon(VaadinIcon.TRASH));
        delete.addClickListener(e -> openDeleteDialog());

        buttonLayout.add(create, edit, delete);
    }

    private void setupGrid() {
        gridDataView = grid.setItems(veranstaltungService.findAll());

        grid.addColumn(Veranstaltung::getId).setHeader("VeranstaltungsID").setKey("id");
        grid.addColumn(Veranstaltung::getBezeichnung).setHeader("Bezeichnung").setKey("bezeichnung");
        grid.addColumn(Veranstaltung::getDozent).setHeader("Dozent").setKey("dozent");
        grid.addColumn(Veranstaltung::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        grid.addColumn(Veranstaltung::getTeilnehmerzahl).setHeader("Teilnehmerzahl").setKey("teilnehmerzahl");

        setupFilters();
    }

    private void setupFilters() {
        VeranstaltungFilter vFilter = new VeranstaltungFilter(gridDataView);

        if (headerRow == null ){
            headerRow = grid.appendHeaderRow();
        }

        Consumer<Set<Fachbereich>> fachbereichFilterChangeConsumer = vFilter::setFachbereich;
        MultiSelectComboBox<Fachbereich> fachbereichComboBox = new MultiSelectComboBox<>();
        fachbereichComboBox.setItems(Fachbereich.values());
        fachbereichComboBox.addValueChangeListener(e -> fachbereichFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("fachbereich")).setComponent(fachbereichComboBox);

        Consumer<Set<Dozent>> dozentFilterChangeConsumer = vFilter::setDozent;
        MultiSelectComboBox<Dozent> dozentComboBox = new MultiSelectComboBox<>();
        dozentComboBox.setItems(dozentService.findAll());
        dozentComboBox.addValueChangeListener(e -> dozentFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("dozent")).setComponent(dozentComboBox);

        Consumer<Integer> teilnehmerFilterChangeConsumer = vFilter::setTeilnehmerzahl;
        IntegerField teilnehmerField = new IntegerField();
        teilnehmerField.setMin(0);
        teilnehmerField.setMax(1000);
        teilnehmerField.setValue(0);
        teilnehmerField.setStepButtonsVisible(true);
        teilnehmerField.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                teilnehmerField.setValue(0);
            }
            teilnehmerFilterChangeConsumer.accept(e.getValue());
        });
        headerRow.getCell(grid.getColumnByKey("teilnehmerzahl")).setComponent(teilnehmerField);

        headerRow.getCell(grid.getColumnByKey("id")).setComponent(createStringFilterHeader(vFilter::setId));
        headerRow.getCell(grid.getColumnByKey("bezeichnung")).setComponent(createStringFilterHeader(vFilter::setBezeichnung));
    }

    private void checkEditDialog() {
        Optional<Veranstaltung> selectedEntry = grid.getSelectionModel().getFirstSelectedItem();

        if (selectedEntry.isPresent()) {
            createDialog(selectedEntry);
        } else {
            Notification.show("Bitte wählen sie einen Eintrag aus!", 2000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private void createDialog(Optional<Veranstaltung> selectedEntry) {
        Dialog dialog = new Dialog();
        FormLayout form = new FormLayout();

        TextField veranstaltungId = new TextField("VeranstaltungsID");

        TextField bezeichnung = new TextField("Bezeichnung");

        ComboBox<Fachbereich> fachbereichComboBox = new ComboBox<>("Fachbereich");
        fachbereichComboBox.setItems(Fachbereich.values());

        ComboBox<Dozent> dozentComboBox = new ComboBox<>("Dozent");
        dozentComboBox.setItems(dozentService.findAll());

        IntegerField teilnehmerzahlField = new IntegerField("Teilnehmerzahl");
        teilnehmerzahlField.setMin(0);
        teilnehmerzahlField.setMax(1000);
        teilnehmerzahlField.setValue(0);
        teilnehmerzahlField.setStepButtonsVisible(true);

        form.add(veranstaltungId, bezeichnung, fachbereichComboBox, dozentComboBox, teilnehmerzahlField);
        dialog.add(form);

        Binder<Veranstaltung> binder = new Binder<>(Veranstaltung.class);
        binder.forField(veranstaltungId).asRequired().bind(Veranstaltung::getId, Veranstaltung::setId);
        binder.forField(bezeichnung).asRequired().bind(Veranstaltung::getBezeichnung, Veranstaltung::setBezeichnung);
        binder.forField(fachbereichComboBox).asRequired("Bitte einen Fachbereich angeben!").bind(Veranstaltung::getFachbereich, Veranstaltung::setFachbereich);
        binder.forField(dozentComboBox).asRequired("Bitte einen Dozenten angeben!").bind(Veranstaltung::getDozent, Veranstaltung::setDozent);
        binder.forField(teilnehmerzahlField).asRequired().bind(Veranstaltung::getTeilnehmerzahl, Veranstaltung::setTeilnehmerzahl);

        selectedEntry.ifPresent(binder::readBean);

        Button saveButton = new Button("Speichern", e -> {
            Veranstaltung veranstaltung = new Veranstaltung();
            if (binder.writeBeanIfValid(veranstaltung)) {
                veranstaltungService.save(veranstaltung);
                refreshGrid();
                //grid.setItems(veranstaltungService.findAll());
                dialog.close();
            } else {
                Notification.show("Bitte alle Felder korrekt befüllen!", 2000, Notification.Position.BOTTOM_CENTER);
            }
        });
        Button cancelButton = new Button("Abbrechen", e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();

    }

    private void openDeleteDialog() {
        Optional<Veranstaltung> selected = grid.getSelectionModel().getFirstSelectedItem();

        if (selected.isPresent()) {
            ConfirmDialog confirmDelete = new ConfirmDialog();

            confirmDelete.setHeader("Veranstaltung " + selected.get().getId() + " - " + selected.get().getBezeichnung() + " löschen?");
            confirmDelete.setText("Diese Aktion kann nicht rückgängig gemacht werden!");

            confirmDelete.setCancelable(true);

            confirmDelete.setCancelButton("Abbrechen", e -> confirmDelete.close());

            confirmDelete.setConfirmButton("Bestätigen", e -> {
                veranstaltungService.delete(selected.get());
                refreshGrid();
                //grid.setItems(veranstaltungService.findAll());
                confirmDelete.close();
            });

            confirmDelete.open();

        } else {
            Notification.show("Bitte wählen sie einen Eintrag aus!", 2000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private void refreshGrid() {
        gridDataView = grid.setItems(veranstaltungService.findAll());
        setupFilters();
    }

    private static class VeranstaltungFilter {
        private final GridListDataView<Veranstaltung> gridDataView;

        private String id;
        private String bezeichnung;
        private Set<Fachbereich> fachbereich;
        private int teilnehmerzahl;
        private Set<Dozent> dozent;

        public VeranstaltungFilter(GridListDataView<Veranstaltung> gridDataView) {
            this.gridDataView = gridDataView;
            this.gridDataView.addFilter(this::createFilter);
        }

        public void setFachbereich(Set<Fachbereich> fachbereich) {
            this.fachbereich = fachbereich;
            this.gridDataView.refreshAll();
        }

        public void setTeilnehmerzahl(int teilnehmerzahl) {
            this.teilnehmerzahl = teilnehmerzahl;
            this.gridDataView.refreshAll();
        }

        public void setDozent(Set<Dozent> dozent) {
            this.dozent = dozent;
            this.gridDataView.refreshAll();
        }

        public void setId(String id) {
            this.id = id;
            this.gridDataView.refreshAll();
        }

        public void setBezeichnung(String bezeichnung) {
            this.bezeichnung = bezeichnung;
            this.gridDataView.refreshAll();
        }

        private boolean createFilter(Veranstaltung v) {
            boolean matchesID = true;
            boolean matchesBez = true;
            boolean matchesFB = true;
            boolean matchesTeiln = true;
            boolean matchesDoz = true;

            matchesID = compare(v.getId(), id);
            matchesBez = compare(v.getBezeichnung(), bezeichnung);
            if (fachbereich != null) {
                matchesFB = compareSet(v.getFachbereich().toString(), fachbereich);
            }
            matchesTeiln = v.getTeilnehmerzahl() >= teilnehmerzahl;
            if (dozent != null) {
                matchesDoz = compareSet(v.getDozent().toString(), dozent);
            }


            return matchesID && matchesBez && matchesFB && matchesTeiln && matchesDoz;
        }

        private boolean compare(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        private boolean compareSet(String value, Set<?> searchTerm) {
            if (searchTerm.isEmpty()) {
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

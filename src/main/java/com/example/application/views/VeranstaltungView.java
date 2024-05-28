package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Route(value = "veranstaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung", "DOZENT"})
@RolesAllowed({"ADMIN", "FBPlanung", "DOZENT"})
@Uses(Icon.class)
@PageTitle("Veranstaltungen")
public class VeranstaltungView extends VerticalLayout {

    private VeranstaltungService veranstaltungService;
    private DozentService dozentService;
    private static BuchungService buchungService;
    private Grid<Veranstaltung> grid;

    public VeranstaltungView(VeranstaltungService veranstaltungService, DozentService dozentService, BuchungService buchungService) {
        this.veranstaltungService = veranstaltungService;
        this.dozentService = dozentService;
        this.buchungService = buchungService;

        addGrid();
        add(grid);
    }
    private void addGrid() {
        Set<Veranstaltung> veranstaltungSet = veranstaltungService.findAll();

        grid = new Grid<>();
        grid.setItems(veranstaltungSet);

        grid.addColumn(Veranstaltung::getId).setHeader("ID").setKey("id");
        grid.addColumn(Veranstaltung::getBezeichnung).setHeader("Bezeichnung").setKey("bezeichnung");
        grid.addColumn(Veranstaltung::getDozent).setHeader("Dozent").setKey("dozent");
        grid.addColumn(Veranstaltung::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        grid.addColumn(Veranstaltung::getTeilnehmerzahl).setHeader("Teilnehmer").setKey("teilnehmerzahl");


        setupFilters(grid.getListDataView());
        grid.setItemDetailsRenderer(createDetailRenderer());
        grid.setDetailsVisibleOnClick(true);

    }
    private static ComponentRenderer<VeranstaltungDetailsFormLayout, Veranstaltung> createDetailRenderer() {
        return new ComponentRenderer<>(VeranstaltungDetailsFormLayout::new, VeranstaltungDetailsFormLayout::linkData);
    }
    private static class VeranstaltungDetailsFormLayout extends VerticalLayout {
        private Grid<Buchung> buchungGrid = new Grid<>();

        public VeranstaltungDetailsFormLayout() {
            buchungGrid.addColumn(Buchung::getRoom).setHeader("Raum");
            buchungGrid.addColumn(Buchung::getDate).setHeader("Datum");
            buchungGrid.addColumn(Buchung::getStartZeit).setHeader("Startzeit");
            buchungGrid.addColumn(Buchung::getEndZeit).setHeader("Endzeit");
            buchungGrid.addColumn(Buchung::getDozent).setHeader("Dozent");

            add(buchungGrid);
        }

        public void linkData(Veranstaltung veranstaltung) {
            buchungGrid.setItems(buchungService.findAllByVeranstaltung(veranstaltung));
        }
    }
    private void setupFilters(GridListDataView<Veranstaltung> gridDataView) {
        VeranstaltungFilter vFilter = new VeranstaltungFilter(gridDataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

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
            if(fachbereich != null) {
                matchesFB = compareSet(v.getFachbereich().toString(), fachbereich);
            }
            matchesTeiln = v.getTeilnehmerzahl() >= teilnehmerzahl;
            if(dozent != null) {
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
    private static Component createStringFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }
}

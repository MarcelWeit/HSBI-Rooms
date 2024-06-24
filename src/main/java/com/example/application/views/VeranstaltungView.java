package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Fachbereich;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.util.*;
import java.util.function.Consumer;

/**
 * View um Veranstaltungen einzusehen. Zusätzlich können beim Klicken auf eine Veranstaltung, die jeweiligen Buchungen
 * zur Veranstaltung angezeigt werden. Veranstaltungen können hier nicht bearbeitet, sondern jediglich eingesehen werden.
 *
 * @author Leon Gepfner
 */
@Route(value = "veranstaltung-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPlanung", "DOZENT"})
@RolesAllowed({"ADMIN", "FBPlanung", "DOZENT"})
@Uses(Icon.class)
@PageTitle("Veranstaltungen")
public class VeranstaltungView extends VerticalLayout {

    private static BuchungService buchungService;
    private final VeranstaltungService veranstaltungService;
    private final DozentService dozentService;
    private Grid<Veranstaltung> grid;

    /**
     * Konstruktur der Klasse VeranstaltungView
     *
     * @param veranstaltungService Service zur Kommunikation mit der Datenbank für die Entität Veranstaltung
     * @param dozentService        Service zur Kommunikation mit der Datenbank für die Entität Dozent
     * @param buchungService       Service zur Kommunikation mit der Datenbank für die Entität Buchung
     */
    public VeranstaltungView(VeranstaltungService veranstaltungService, DozentService dozentService, BuchungService buchungService) {
        this.veranstaltungService = veranstaltungService;
        this.dozentService = dozentService;
        VeranstaltungView.buchungService = buchungService;

        addGrid();
        add(grid);
    }

    /**
     * Erstellt einen Komponenten Renderer für die Detail Ansicht der Veranstaltungsdatensätze
     *
     * @return Komponenten Renderer
     */
    private static ComponentRenderer<VeranstaltungDetailsFormLayout, Veranstaltung> createDetailRenderer() {
        return new ComponentRenderer<>(VeranstaltungDetailsFormLayout::new, VeranstaltungDetailsFormLayout::linkData);
    }

    /**
     * Erstellt ein Textfeld zur Filterung von Textbasierten Spalten
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
     * Erstellt das Grid für den VeranstaltungsView in dem die Veranstaltungen abbgebildet werden.
     */
    private void addGrid() {
        Set<Veranstaltung> veranstaltungSet = veranstaltungService.findAll();

        grid = new Grid<>();
        grid.setItems(veranstaltungSet);

        grid.addColumn(Veranstaltung::getId).setHeader("ID").setKey("id").setSortable(true);
        grid.addColumn(Veranstaltung::getBezeichnung).setHeader("Bezeichnung").setKey("bezeichnung").setSortable(true);
        grid.addColumn(Veranstaltung::getDozent).setHeader("Dozent").setKey("dozent").setSortable(true);
        grid.addColumn(Veranstaltung::getFachbereich).setHeader("Fachbereich").setKey("fachbereich").setSortable(true);
        grid.addColumn(Veranstaltung::getTeilnehmerzahl).setHeader("Teilnehmer").setKey("teilnehmerzahl").setSortable(true);

        GridSortOrder<Veranstaltung> sortOrderVeranstaltung = new GridSortOrder<>(grid.getColumnByKey("id"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<Veranstaltung>> sortOrder = new ArrayList<>();
        sortOrder.add(sortOrderVeranstaltung);
        grid.sort(sortOrder);


        setupFilters(grid.getListDataView());
        grid.setItemDetailsRenderer(createDetailRenderer());
        grid.setDetailsVisibleOnClick(true);

        grid.setMinHeight("80vh");

    }

    /**
     * Methode zum Selektieren der Filterwerte
     * Es werden nur Filterwerte angezeigt, die den Tabellendaten entsprechen
     *
     * @param data Alle möglichen Filterwerte
     * @return Modifiziertes Set, welches die selektieren Filterwerte enthält
     */
    private Set<?> selectFilterData(Set<?> data) {
        GridListDataView<Veranstaltung> dataView = grid.getListDataView();
        List<Dozent> helpListDozent = dataView.getItems().map(Veranstaltung::getDozent).toList();
        List<Fachbereich> helpListFachbereich = dataView.getItems().map(Veranstaltung::getFachbereich).toList();

        List<String> buchungsDozent = helpListDozent.stream().map(Dozent::toString).toList();
        List<String> buchungsFachbereich = helpListFachbereich.stream().map(Fachbereich::toString).toList();

        Set<?> result = new HashSet<>(Set.copyOf(data));

        for (Object item : data) {
            if (item instanceof Dozent) {
                if (!buchungsDozent.contains(item.toString())) {
                    result.remove(item);
                }
            } else if (item instanceof Fachbereich) {
                if (!buchungsFachbereich.contains(item.toString())) {
                    result.remove(item);
                }
            }
        }
        return result;
    }

    /**
     * Erstellt die Filter des Grids zum Selektieren von Datensätzen
     *
     * @param gridDataView Data View für die Veranstaltungen
     */
    private void setupFilters(GridListDataView<Veranstaltung> gridDataView) {
        VeranstaltungFilter vFilter = new VeranstaltungFilter(gridDataView);
        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

        Consumer<Set<Fachbereich>> fachbereichFilterChangeConsumer = vFilter::setFachbereich;
        MultiSelectComboBox<Fachbereich> fachbereichComboBox = new MultiSelectComboBox<>();
        fachbereichComboBox.setItems((Set<Fachbereich>) selectFilterData(Set.of(Fachbereich.values())));
        fachbereichComboBox.addValueChangeListener(e -> fachbereichFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(grid.getColumnByKey("fachbereich")).setComponent(fachbereichComboBox);

        Consumer<Set<Dozent>> dozentFilterChangeConsumer = vFilter::setDozent;
        MultiSelectComboBox<Dozent> dozentComboBox = new MultiSelectComboBox<>();
        dozentComboBox.setItems((Set<Dozent>) selectFilterData(Set.of(dozentService.findAll())));
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

    /**
     * Interne Klasse um die Detail Ansichten für die Veranstaltungen zu realisieren. Detail Ansicht zeigt
     * ein Grid mit allen Buchungen der Veranstaltung
     */
    private static class VeranstaltungDetailsFormLayout extends VerticalLayout {
        private final Grid<Buchung> buchungGrid = new Grid<>();

        /**
         * Konstruktur der Internen Klasse
         */
        public VeranstaltungDetailsFormLayout() {
            buchungGrid.addColumn(Buchung::getRoom).setHeader("Raum");
            buchungGrid.addColumn(Buchung::getDate).setHeader("Datum");
            buchungGrid.addColumn(Buchung::getZeitslot).setHeader("Zeitslot");
            buchungGrid.addColumn(Buchung::getDozent).setHeader("Dozent");

            add(buchungGrid);
        }

        /**
         * Setzt die Buchungen des Grids für die jeweilig übergebene Veranstaltung
         *
         * @param veranstaltung Angeklickte Veranstaltung
         */
        public void linkData(Veranstaltung veranstaltung) {
            buchungGrid.setItems(buchungService.findAllByVeranstaltung(veranstaltung));
        }
    }

    /**
     * Interne Klasse zur Realisierung der Filterfunktion. Klasse speichert die Filterwerte ab, damit diese
     * zur Selektierung der Datensätze verwendet werden kann.
     */
    private static class VeranstaltungFilter {
        private final GridListDataView<Veranstaltung> gridDataView;

        private String id;
        private String bezeichnung;
        private Set<Fachbereich> fachbereich;
        private int teilnehmerzahl;
        private Set<Dozent> dozent;

        /**
         * Konstruktur der Internen Klasse VeranstaltungFilter
         *
         * @param gridDataView Data View für Veranstaltungen
         */
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

        /**
         * Realisiert den Abgleich einer Veranstaltung mit den aktuellen Filterwerten
         *
         * @param v Veranstaltungsdatensatz die abgeglichen werden soll
         * @return boolean Wert ob Veranstaltung Filtern entspricht
         */
        private boolean createFilter(Veranstaltung v) {
            boolean matchesID = true;
            boolean matchesBez = true;
            boolean matchesFB = true;
            boolean matchesTeiln = true;
            boolean matchesDoz = true;

            matchesID = compare(v.getId(), id);
            matchesBez = compare(v.getBezeichnung(), bezeichnung);
            matchesFB = compareSet(v.getFachbereich().toString(), fachbereich);

            matchesTeiln = v.getTeilnehmerzahl() >= teilnehmerzahl;
            matchesDoz = compareSet(v.getDozent().toString(), dozent);


            return matchesID && matchesBez && matchesFB && matchesTeiln && matchesDoz;
        }

        /**
         * Methode zum vergleichen von 2 Strings
         *
         * @param value      Vergleichswert
         * @param searchTerm Vergleichswert
         * @return boolean Wert ob Werte übereinstimmen
         */
        private boolean compare(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        /**
         * Methode zum überprüfen ob value im übergebenen Set enthalten ist
         *
         * @param value      Vergleichswert
         * @param searchTerm Zu überprüfendes Set
         * @return boolean Wert ob value im Set zu finden ist
         */
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
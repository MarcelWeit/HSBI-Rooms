package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Raum;
import com.example.application.services.BuchungService;
import com.example.application.services.RaumService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.helper.Series;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * View für die Anzeige der Raumauslastung
 *
 * @author Marcel Weithoener, Gabriel Greb
 */
@Route(value = "kapa", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "FBPLANUNG", "DOZENT"})
@PageTitle("Kapazitäten")
public class AuslastungView extends VerticalLayout {

    private final BuchungService buchungService;
    private final RaumService raumService;
    private final DatePicker endDatePicker = new DatePicker("EndDatum");
    private final DatePicker startDatePicker = new DatePicker("StartDatum");
    private final Checkbox weeekendCheckbox = new Checkbox("Samstag/Sonntag ausschließen");
    private Grid<RaumAuslastung> grid;
    private ApexCharts stackedColumnChart;
    HorizontalLayout mainLayout;

    public AuslastungView(BuchungService buchungService, RaumService raumService) {
        this.raumService = raumService;
        this.buchungService = buchungService;

        initializeComponents();  // Initialisiert die Komponenten
        setupInteractionBar();  // Richtet die Interaktionsleiste ein
        setupLayout();          // setzt das Layout
        setupGrid(raumService);  // Grid einrichten
    }

    //initialisierung des Grids/Diagramms
    private void initializeComponents() {
        // Grid initialisieren
        grid = new Grid<>();
        grid.setAllRowsVisible(true);

        // Initialisierung des gestapelten Balkendiagramms (stackedColumnChart)
        stackedColumnChart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(com.github.appreciated.apexcharts.config.chart.Type.BAR) // Setzen des Diagrammtyps auf Balkendiagramm
                        .withForeColor("#808080")   //Achsenbeschriftung auf Mittegrau setzten
                        .withStacked(true)          //Gestapelte darstellung aktivieren
                        .withToolbar(ToolbarBuilder.get()
                                .withShow(false)  // Deaktiviert zusätzliche funktionen
                                .build())
                        .build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get()
                                .withHorizontal(false) //Vertikale Ausrichtung der Balken
                                .build())
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withMax(100.0) //festlegen des maximalen Wertes der Y-Achse (100%)
                        .withLabels(LabelsBuilder.get()
                                .withFormatter("function(val) { return val + '%' }") //hinzufügen von Prozentzeichen an die Y-Achsenwerte
                                .build())
                        .build())
                .withLegend(LegendBuilder.get()
                        .withShowForSingleSeries(true) // Anzeigen der Legende auch bei nur einer Veranstaltung
                        .build())
                .withTooltip(TooltipBuilder.get()
                        // Anpassung der Tooltips mit HTML
                        .withCustom("function({ series, seriesIndex, dataPointIndex, w }) {" +
                                "const label = w.globals.labels[dataPointIndex];" +
                                "const color = w.globals.colors[seriesIndex];" +
                                "const value = series[seriesIndex][dataPointIndex];" +
                                "const seriesName = w.config.series[seriesIndex].name;" +
                                "return '<div style=\"padding: 5px; color: black; background-color: white; border-radius: 5px;\">' +" +
                                "'<div><strong style=\"color: black;\">' + label + '</strong></div>' +" +
                                "'<div style=\"display: flex; align-items: center; margin-top: 5px;\">' +" +
                                "'<div style=\"width: 10px; height: 10px; background-color: ' + color + '; border-radius: 50%; margin-right: 5px;\"></div>' +" +
                                "'<div style=\"color: black;\">' + seriesName + ': <strong>' + value.toFixed(2) + '%</strong></div>' +" +
                                "'</div></div>';}")
                        .build())
                .build();
    }

    //einrichten der Datumseingabefelder
    private void setupInteractionBar() {
        // Setzt das Datum auf das aktuelle Datum
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        // Hinzufügen von ValueChangeListener, um das Grid zu aktualisieren, wenn sich ein Datumsfeld oder die Checkbox aktualisiert
        startDatePicker.addValueChangeListener(event -> updateGrid(event.getValue(), endDatePicker.getValue(), weeekendCheckbox.getValue()));
        endDatePicker.addValueChangeListener(event -> updateGrid(startDatePicker.getValue(), event.getValue(), weeekendCheckbox.getValue()));
        weeekendCheckbox.addValueChangeListener(event -> updateGrid(startDatePicker.getValue(), endDatePicker.getValue(), weeekendCheckbox.getValue()));

        // Erstellt ein Layout für die DatePicker und die Checkbox
        HorizontalLayout dateLayout = new HorizontalLayout(startDatePicker, endDatePicker, weeekendCheckbox);
        // Richtet die Elemente aus
        dateLayout.setAlignItems(Alignment.BASELINE);
        // Fügt die Interaktionsleiste dem Hauptcontainer hinzu
        add(dateLayout);
    }

    private void setupLayout() {
        mainLayout = new HorizontalLayout();                        //initalsierung
        mainLayout.setWidthFull();                                  //layout wird auf die volle breite gesetzt
        mainLayout.getStyle().set("overflow", "hidden");            //verhindert horizontales scrollen

        if (grid != null && stackedColumnChart != null) {           //prüft ob das Grid/Diagramm initialsiert ist
            grid.getElement().getStyle().set("width", "auto");      // passt die breite des Grids automatisch an
            grid.setHeight("400px");                                // höhe des grids wird auf 400px gesetzt
            stackedColumnChart.setHeight("400px");                  // höhe des Diagramms wird auf 400 px gesetzt
            mainLayout.add(grid, stackedColumnChart);               // Grid/Diagramm werden dem layout hinzugefügt
            mainLayout.setFlexGrow(1, grid);                //setzt den vergrößerungsfaktor für das Grid
            mainLayout.setFlexGrow(1, stackedColumnChart);  //setzt den vergrößerungsfaktor für das Diagramm
        } else {
            System.err.println("Grid or Chart is not initialized.");
        }
        // Fügt das Hauptlayout dem Hauptcontainer hinzu
        add(mainLayout);
    }


    //einrichten des Grids
    private void setupGrid(RaumService raumService) {

        // Hinzufügen einer Spalte zum Grid für die Anzeige des Raumnamens
        grid.addColumn(RaumAuslastung::raumToString)
                .setKey("Raum")
                .setHeader("Raum")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setSortable(true)
                .setFooter("Anzahl Räume: " + raumService.count());

        // Hinzufügen einer Spalte zum Grid für die Anzeige der Auslastung
        grid.addColumn(RaumAuslastung::getAuslastungAsString)
                .setKey("Auslastung")
                .setHeader("Auslastung")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setSortable(true);

        // Aktualisiert das Grid mit den aktuellen Daten
        updateGrid(LocalDate.now(), LocalDate.now(), false);
    }

    private void updateGrid(LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {
        Set<Raum> raeume = raumService.findAll();                       //holen aller Räume
        List<RaumAuslastung> raumAuslastungen = new ArrayList<>();      //liste für die Raumauslastung

        //Fügt für jeden Raum eine Raumauslastung hinzu
        for (Raum raum : raeume) {
            raumAuslastungen.add(new RaumAuslastung(raum, buchungService, startDate, endDate, ignoreWeekends));
        }

        //addiert alle Raumauslastungen auf
        double totalAuslastung = 0;
        for (RaumAuslastung raumAuslastung : raumAuslastungen) {
            totalAuslastung += raumAuslastung.getAuslastung();
        }

        //berechnet die prozentuale Gesamtauslastung und gibt sie unten im Grid an
        double totalAuslastungInPercent = totalAuslastung / raumAuslastungen.size();
        grid.getColumnByKey("Auslastung").setFooter("Gesamtauslastung: " + String.format("%.2f", totalAuslastungInPercent) + " %");
        grid.setItems(raumAuslastungen);

        // Überprüft die prozentuale Gesamtauslastung und aktualisiert das Diagramm nur, wenn sie größer als 0 ist
        if (totalAuslastungInPercent > 0) {
            updateChart(raumAuslastungen);  //aktualsiert das Diagramm
            // Fügt das Diagramm zum Hauptlayout hinzu, falls es nicht bereits vorhanden ist
            if (!mainLayout.getChildren().anyMatch(component -> component.equals(stackedColumnChart))) {
                mainLayout.add(stackedColumnChart);
            }
        } else {
            // Entfernt das Diagramm aus dem Hauptlayout, wenn die durchschnittliche Auslastung 0 ist
            mainLayout.remove(stackedColumnChart);
        }
    }

    //Methode zum aktualisieren des Diagramms
    private void updateChart(List<RaumAuslastung> raumAuslastungen) {
        if (stackedColumnChart == null) {
            // Gibt eine Fehlermeldung aus, wenn das Diagramm nicht initialisiert ist
            System.err.println("stackedColumnChart is not initialized.");
            return;
        }

        // Map zur Speicherung der Veranstaltungen pro Raum
        Map<String, Map<String, Double>> raumVeranstaltungen = new HashMap<>();

        // Iteriere durch jede Raumauslastung
        for (RaumAuslastung raumAuslastung : raumAuslastungen) {
            String raumName = raumAuslastung.raumToString();

            // Füge den Raum zur Map hinzu, falls noch nicht vorhanden
            raumVeranstaltungen.putIfAbsent(raumName, new HashMap<>());

            // Map zur Speicherung der Anzahl der Buchungen pro Veranstaltung für den aktuellen Raum
            Map<String, Double> veranstaltungen = raumVeranstaltungen.get(raumName);

            LocalDate currentDate = raumAuslastung.startDate; // Setze das Startdatum für die Iteration
            int totalDays = 0;                                // Anzahl der Tage im Zeitraum

            // Iteriere durch die Tage im gegebenen Zeitraum
            while (!currentDate.isAfter(raumAuslastung.endDate)) {
                // Berücksichtige Wochenenden, wenn diese nicht ausgeschlossen sind. Ansonsten berücksichtige nur die Tage Mo-Fr
                if (!raumAuslastung.ignoreWeekends || (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY)) {
                    // Finde alle Buchungen für den aktuellen Tag und Raum
                    Set<Buchung> buchungen = raumAuslastung.buchungService.findAllByDateAndRoom(currentDate, raumAuslastung.raum);
                    // Iteriere über jede Buchung am aktuellen Tag und im aktuellen Raum
                    for (Buchung buchung : buchungen) {
                        String veranstaltungName = buchung.getVeranstaltung().getBezeichnung(); // Hole den Namen der Veranstaltung aus der Buchung
                        // Aktualisiere die Anzahl der Buchungen für diese Veranstaltung
                        veranstaltungen.put(veranstaltungName, veranstaltungen.getOrDefault(veranstaltungName, 0.0) + 1);
                    }
                    totalDays++; // Erhöhe die Anzahl der berücksichtigten Tage
                }
                currentDate = currentDate.plusDays(1);// Erhöhe das aktuelle Datum um einen Tag
            }

            // Berechnen der Auslastung pro Veranstaltung
            for (Map.Entry<String, Double> entry : veranstaltungen.entrySet()) {
                double anzahlBuchungen = entry.getValue();                              //Anzahl Buchungen holen für die aktuelle Veranstaltung
                double prozentAuslastung = (anzahlBuchungen / (totalDays * 7.0)) * 100; //Berechnung des prozentualen Anteils der Buchung
                prozentAuslastung = Math.round(prozentAuslastung * 100.0) / 100.0;       // Runden auf zwei Nachkommastellen
                veranstaltungen.put(entry.getKey(), prozentAuslastung); // Aktualisieren der Map mit der gerundeten prozentualen Auslastung
            }
        }

        // Entfernt Räume ohne Auslastung
        raumVeranstaltungen.entrySet().removeIf(entry -> entry.getValue().values().stream().allMatch(value -> value == 0.0));

        // Liste der Datenreihen für das Diagramm
        List<Series<Number>> seriesList = new ArrayList<>();
        // Sammlung aller Veranstaltungnamen
        Set<String> veranstaltungNamen = raumVeranstaltungen.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet());

        // Erstellen der Datenreihen basierend auf den Veranstaltungen
        for (String veranstaltungName : veranstaltungNamen) {
            // Liste für die Daten der aktuellen Veranstaltung
            List<Number> data = new ArrayList<>();
            // Iteriere über jeden Raumnamen
            for (String raumName : raumVeranstaltungen.keySet()) {
                // Hole die Map der Veranstaltungen für den aktuellen Raum
                Map<String, Double> veranstaltungen = raumVeranstaltungen.get(raumName);
                // Füge die Auslastung der aktuellen Veranstaltung zur Datenliste hinzu, falls vorhanden, sonst 0.0
                data.add(veranstaltungen.getOrDefault(veranstaltungName, 0.0));
            }
            // Füge die neue Datenreihe zur Liste der Datenreihen hinzu
            seriesList.add(new Series<>(veranstaltungName, data.toArray(new Number[0])));
        }

        // Erstellen der Kategorien für die X-Achse basierend auf den Raumnamen
        String[] categories = raumVeranstaltungen.keySet().toArray(new String[0]);

        // Aktualisiert die Datenreihen und die X-Achse des Diagramms
        stackedColumnChart.updateSeries(seriesList.toArray(new Series[0]));
        stackedColumnChart.setXaxis(XAxisBuilder.get().withCategories(categories).build());
    }


    //Berechnet und speichert die Auslastung eines Raums über einen bestimmten Zeitraum.
    private record RaumAuslastung(Raum raum, BuchungService buchungService, LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {

        public String raumToString() {
            return raum.toString();
        }

        public String getAuslastungAsString() {
            return String.format("%.2f", getAuslastung()) + " %";
        }

        public double getAuslastung() {
            LocalDate currentDate = startDate;
            double auslastung = 0;
            int daycount = 0;
            // Iteriere über jeden Tag im Zeitraum
            while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                // Überspringe Wochenenden, wenn ignoreWeekends auf true gesetzt ist
                if (ignoreWeekends && (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
                    currentDate = currentDate.plusDays(1);
                    continue;
                }
                // Berechne die Auslastung für den aktuellen Tag und addiere sie zur Gesamtauslastung
                auslastung += calculateAuslastung(buchungService.findAllByDateAndRoom(currentDate, raum));
                currentDate = currentDate.plusDays(1);
                daycount++;
            }
            return auslastung / daycount;
        }
        //Berechnet die Auslastung basierend auf der Anzahl an Buchungen
        private double calculateAuslastung(Set<Buchung> buchungen) {
            return buchungen.size() / 7.0 * 100;
        }
    }
}
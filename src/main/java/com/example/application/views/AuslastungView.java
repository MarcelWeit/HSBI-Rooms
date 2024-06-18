package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Raum;
import com.example.application.services.BuchungService;
import com.example.application.services.RaumService;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.subtitle.builder.StyleBuilder;
import com.nimbusds.jose.shaded.gson.Gson;
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
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.yaxis.builder.LabelsBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.component.UI;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private Grid<RaumAuslastung> grid;
    private ApexCharts lineChart;
    private ApexCharts pieChart;
    HorizontalLayout mainLayout;
    VerticalLayout chartLayout;

    //Konstruktor
    public AuslastungView(BuchungService buchungService, RaumService raumService) {
        this.raumService = raumService;
        this.buchungService = buchungService;

        initializeComponents(); // Initialisiert die Komponenten
        setupLayout();          // setzt das Layout
        setupGrid(raumService); // Grid einrichten --> ruft updateGrid auf --> ruft updateLineChart und updatePieCharts auf
    }

    //Methode zur Initialisierung der Komponenten
    private void initializeComponents() {
        // Grid initialisieren
        grid = new Grid<>();
        grid.setAllRowsVisible(true);

        LocalDate startDate = LocalDate.now().minusDays(30); //Startdatum ist das aktuelle Datum minus 30 Tage
        LocalDate endDate = LocalDate.now();                              //Endatum ist das aktuelle Datum

        // Initialisierung des Liniendiagramms/Tortendiagramms mit Standartwerten
        lineChart = setupLineChart(new String[0], new Double[0], startDate, endDate, false);
        pieChart = setupPieChart(new HashMap<>());

        setupInteractionBar();  // Richtet die Interaktionsleiste ein
    }

    //Konfiguration des Layouts
    private void setupLayout() {
        mainLayout = new HorizontalLayout();                        // Initalsierung des Horizontalen Layouts
        mainLayout.setWidthFull();                                  // Layout wird auf die volle breite gesetzt
        mainLayout.getStyle().set("overflow", "hidden");            // Verhindert horizontales scrollen

        if (grid != null && lineChart != null) {                    // Prüft ob das Grid/Linieniagramm initialsiert ist
            grid.getElement().getStyle().set("width", "auto");      // Passt die breite des Grids automatisch an
            grid.setHeight("400px");                                // Höhe des grids wird auf 400px gesetzt
            lineChart.setHeight("400px");                           // Höhe des Linieniagramms wird auf 400 px gesetzt
            mainLayout.setFlexGrow(1, grid);               // Setzt den Vergrößerungsfaktor für das Grid
            mainLayout.setFlexGrow(1, lineChart);          // Setzt den Vergrößerungsfaktor für das Linieniagramm
            mainLayout.add(grid, lineChart);                       // Grid/Linieniagramm werden dem layout hinzugefügt

        } else {
            System.err.println("Grid or Chart is not initialized.");
        }

        add(mainLayout); // Fügt das Layout dem Hauptcontainer hinzu

        // Löst nach 100ms ein 'resize'-Event aus, um Layout- und Anzeigefehler zu vermeiden
        UI.getCurrent().getPage().executeJs("setTimeout(function() { window.dispatchEvent(new Event('resize')); }, 100);");
    }

    //Konfiguration der Interagtionsleiste (Datumsfelder/Checkbox)
    private void setupInteractionBar() {
        DatePicker endDatePicker = new DatePicker("EndDatum");      //Datumseingabefeld (Startdatum)
        DatePicker startDatePicker = new DatePicker("StartDatum");  //Datumseingabefeld (Enddatum)
        Checkbox weeekendCheckbox = new Checkbox("Samstag/Sonntag ausschließen");   //Checkbox um das Wochenende ausschließen zu können

        startDatePicker.setValue(LocalDate.now().minusDays(30));    //startdatum ist das aktuelle Datum minus 30 Tage
        endDatePicker.setValue(LocalDate.now());                                 //endatum ist das aktuelle Datum

        // Hinzufügen von ValueChangeListener, um das Grid zu aktualisieren, wenn sich ein Datumsfeld oder die Checkbox aktualisiert
        startDatePicker.addValueChangeListener(event -> updateGrid(event.getValue(), endDatePicker.getValue(), weeekendCheckbox.getValue()));
        endDatePicker.addValueChangeListener(event -> updateGrid(startDatePicker.getValue(), event.getValue(), weeekendCheckbox.getValue()));
        weeekendCheckbox.addValueChangeListener(event -> updateGrid(startDatePicker.getValue(), endDatePicker.getValue(), weeekendCheckbox.getValue()));

        // Erstellt ein Layout für die DatePicker und die Checkbox
        HorizontalLayout dateLayout = new HorizontalLayout(startDatePicker, endDatePicker, weeekendCheckbox);
        dateLayout.setAlignItems(Alignment.BASELINE); // Richtet die Elemente aus
        add(dateLayout); // Fügt die Interaktionsleiste dem Hauptcontainer hinzu
    }


    //Konfiguration des Grids
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
        updateGrid(LocalDate.now().minusDays(30), LocalDate.now(), false);
    }

    /**
     * Setzt das Liniendiagramm mit den gegebenen Parametern auf.
     *
     * @param xLabels       Die Labels, die auf der X-Achse angezeigt werden sollen.
     * @param seriesData    Die Datenwerte, die im Diagramm dargestellt werden sollen.
     * @param startDate     Das Startdatum des Zeitraums.
     * @param endDate       Das Enddatum des Zeitraums.
     * @param ignoreWeekends Ob Wochenenden ignoriert werden sollen.
     * @return Eine konfigurierte Instanz von ApexCharts für das Liniendiagramm.
     */
    private ApexCharts setupLineChart(String[] xLabels, Double[] seriesData, LocalDate startDate, LocalDate endDate, Boolean ignoreWeekends) {
        List<String> allDates = generateAllDates(startDate, endDate, ignoreWeekends); //holen aller Daten im Zeitraum

        injectCustomStyles(); //konfiguriert die Toolbar des Diagramms mithilfe von CSS

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(com.github.appreciated.apexcharts.config.chart.Type.LINE) // Setzen des Diagrammtyps auf Liniendiagramm
                        .withForeColor("#808080")               //setzt die Achsenbeschriftung auf grau
                        .withToolbar(ToolbarBuilder.get()       //zeigt zusätzliche Funktionen an (Zoom/Download)
                                .withShow(true)
                                .build())
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Gesamtauslastung pro Tag im Zeitverlauf")    //setzt eine Überschrift für das Diagramm
                        .withAlign(Align.CENTER)
                        .withStyle(StyleBuilder.get()
                                .withColor("DodgerBlue")
                                .build())
                        .build())
                .withYaxis(YAxisBuilder.get()                                   //Konfiguration der Y-Achse
                        .withMax(100.0)
                        .withLabels(LabelsBuilder.get()
                                .withFormatter("function(val) { return val + '%' }")
                                .build())
                        .build())
                .withTooltip(TooltipBuilder.get()                               //Konfiguration des Tooltips mit HTML und CSS
                        .withCustom("function({ series, seriesIndex, dataPointIndex, w }) {" +
                                "const allDates = " + new Gson().toJson(allDates) + ";" + // Alle Daten als JSON-Array
                                "const date = allDates[dataPointIndex];" +
                                "const value = series[seriesIndex][dataPointIndex];" +
                                "const seriesName = w.config.series[seriesIndex].name;" +
                                "return '<div style=\"padding: 5px; color: black; background-color: white; border-radius: 5px;\">' +" +
                                "'<div><strong style=\"color: black;\">' + date + '</strong></div>' +" +
                                "'<div style=\"color: black;\">' + seriesName + ': <strong>' + value.toFixed(2) + '%</strong></div>' +" +
                                "'</div></div>';}")
                        .build())
                .withXaxis(XAxisBuilder.get()       //Konfiguration der X-Achse
                        .withCategories(xLabels)
                        .build())
                .withSeries(new Series<>("Gesamtauslastung", seriesData)) //Hinzufügen einer neuen Datenreihe
                .build();
    }

     // Wird von setupLineChart aufgerufen
     // Generiert eine Liste aller Datumsangaben zwischen zwei angegebenen Daten
    private List<String> generateAllDates(LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {
        List<String> allDates = new ArrayList<>();      // Liste zur Speicherung der Datumsangaben
        LocalDate currentDate = startDate;              // Initialisiert das aktuelle Datum mit dem Startdatum
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //Formartierung des Datums

        // Iteriere über jeden Tag im Zeitraum
        while (!currentDate.isAfter(endDate)) {
            // Überspringe Wochenendtage, wenn ignoreWeekends auf true gesetzt ist
            if (ignoreWeekends && (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                currentDate = currentDate.plusDays(1);
                continue; // Überspringe Wochenendtage
            }
            // Füge das aktuelle Datum im formatieren String zur Liste hinzu
            allDates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1); // Erhöht das aktuelle Datum um einen Tag
        }

        return allDates;
    }

     // Wird von setupLineChart aufgerufen
     // Konfiguriert die Toolbar des Liniendiagramms mit CSS
    private void injectCustomStyles() {
        // CSS für die Toolbar und den Download-Button
        String customCSS = ""
                + ".apexcharts-toolbar { color: blue !important; }" // Toolbar-Textfarbe
                + ".apexcharts-menu-item:hover { color: red !important; }" // Hover-Farbe für Menü-Items
                + ".apexcharts-menu-item { color: black !important; }"; // Standard-Textfarbe für Menü-Items

        Element styleElement = new Element("style");
        styleElement.setText(customCSS);
        UI.getCurrent().getElement().appendChild(styleElement);
    }

    /**
     * Konfiguration des Tortendiagramms mit den gegebenen Parametern.
     *
     * @param eventAuslastung Eine Map, die die Auslastung pro Veranstaltung enthält.
     *                        Der Schlüssel ist der Name der Veranstaltung, der Wert ist die Auslastung in Prozent.
     * @return Eine konfigurierte Instanz von ApexCharts für das Tortendiagramm.
     */
    private ApexCharts setupPieChart(Map<String, Double> eventAuslastung) {
        List<String> labels = new ArrayList<>(eventAuslastung.keySet()); // Veranstaltungsnamen
        List<Double> values = new ArrayList<>(eventAuslastung.values()); // Auslastungswerte

        return ApexChartsBuilder.get()
                .withChart(ChartBuilder.get()
                        .withType(Type.PIE)         // Setzen des Diagrammtyps auf Tortendiagramm
                        .withForeColor("#808080")   //setzt die Achsenbeschriftung auf grau
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText("Gesamtauslastung nach Veranstaltung")    //setzt eine Überschrift für das Diagramm
                        .withAlign(Align.CENTER)
                        .withStyle(StyleBuilder.get()
                                .withColor("DodgerBlue")
                                .build())
                        .build())
                .withLabels(labels.toArray(new String[0]))  // Setzen der Veranstaltungsnamen
                .withSeries(values.toArray(new Double[0]))  // Setzen der Auslastungswerte
                .withLegend(LegendBuilder.get()
                        .withPosition(com.github.appreciated.apexcharts.config.legend.Position.BOTTOM) // Positionieren der Legende unten
                        .build())
                .build();
    }

    /**
     * Aktualisiert das Grid und die Diagramme basierend auf den gegebenen Parametern.
     *
     * @param startDate      Das Startdatum des Zeitraums.
     * @param endDate        Das Enddatum des Zeitraums.
     * @param ignoreWeekends Ob Wochenenden ausgeschlossen werden sollen.
     */
    private void updateGrid(LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {
        Set<Raum> raeume = raumService.findAll();                       //Holen aller Räume
        List<RaumAuslastung> raumAuslastungen = new ArrayList<>();      //Liste für die Raumauslastung

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

        // Überprüft ob die prozentuale Gesamtauslastung größer als 0 ist und aktualisiert die Diagramme nur wenn sie größer ist
        if (totalAuslastungInPercent > 0) {
            //Aktualsieren der Diagramme, wenn die Gesamtauslastung größer 0 ist
            updateLineChart(startDate, endDate, ignoreWeekends, raumAuslastungen);
            updatePieChart(raumAuslastungen, totalAuslastungInPercent, startDate, endDate, ignoreWeekends);
        } else {
            // Entfernt die Diagramme, wenn die Gesamtauslastung 0 ist
            mainLayout.remove(lineChart);
            if (mainLayout != null && chartLayout != null){
                remove(chartLayout);
            }
        }
    }

    /**
     * Methode zum Aktualisieren des Liniendiagramms basierend auf den gegebenen Parametern.
     *
     * @param startDate        Das Startdatum des Zeitraums.
     * @param endDate          Das Enddatum des Zeitraums.
     * @param ignoreWeekends   Gibt an ob Wochenenden ausgeschlossen werden sollen.
     * @param raumAuslastungen Eine Liste von RaumAuslastung-Objekten, die die Auslastungsdaten enthalten.
     */
    private void updateLineChart(LocalDate startDate, LocalDate endDate, boolean ignoreWeekends, List<RaumAuslastung> raumAuslastungen) {
        if (lineChart == null) {
            System.err.println("lineChart is not initialized.");
            return;
        }

        // Map zum Speichern der Auslastung pro Datum
        Map<LocalDate, Double> dateToAuslastung = new LinkedHashMap<>();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Berechnet die anzahl Tage zwischen start und endDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //Formartiert das Datum

        // Iterieren über den Zeitraum von startDate bis endDate
        LocalDate currentDate = startDate;
        for (int i = 0; i < totalDays; i++) {
            // Überprüfen, ob Wochenenden ignoriert werden sollen
            if (ignoreWeekends && (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                currentDate = currentDate.plusDays(1); // Datum um einen Tag erhöhen
                continue; // Schleifeniteration überspringen, um Wochenenden zu ignorieren
            } else {

                double sumAuslastung = 0.0; // Summe der Auslastungen für den aktuellen Tag
                int anzReaume = 0;              // Zähler für die Anzahl der Räume

                // Iteration über alle RaumAuslastungen, um die Auslastung für den Tag zu berechnen
                for (RaumAuslastung raumAuslastung : raumAuslastungen) {
                    sumAuslastung += raumAuslastung.getDailyAuslastung(currentDate); // Hinzufügen der täglichen Auslastung des Raumes zur täglichen Gesamtauslastung
                    anzReaume++; // Erhöhen des Zählers
                }

                // Berechnung der prozentualen Auslastung für das aktuelle Datum
                double totalAuslastung =  anzReaume > 0 ? sumAuslastung /  anzReaume : 0.0; // Vermeiden von Division durch Null, falls count 0 ist

                // Speichern der prozentualen Auslastung für das aktuelle Datum in der Map
                dateToAuslastung.put(currentDate, totalAuslastung);
            }

            currentDate = currentDate.plusDays(1); // Datum um einen Tag erhöhen, um zum nächsten Tag überzugehen
        }

        // Erstellen von Listen für die Daten und Auslastungswerte
        List<LocalDate> dates = new ArrayList<>(dateToAuslastung.keySet());
        dates.sort(Comparator.naturalOrder());
        List<Double> auslastungList = new ArrayList<>(dateToAuslastung.values());

        // Bestimmen der Schrittweite für die X-Achsenbeschriftungen
        int step = dates.size() > 6 ? (int) Math.ceil((double) dates.size() / 6) : 1;
        String[] xLabels = new String[dates.size()];
        for (int i = 0; i < dates.size(); i++) {
            xLabels[i] = (i % step == 0 || i == dates.size() - 1) ? dates.get(i).format(formatter) : "";
        }

        // Sicherstellen, dass das erste und letzte Label gesetzt sind
        xLabels[0] = dates.get(0).format(formatter);
        xLabels[xLabels.length - 1] = dates.get(dates.size() - 1).format(formatter);

        // Entfernen des vorhandenen Diagramms
        mainLayout.remove(lineChart);

        // Erstellen und Hinzufügen des neuen Liniendiagramms
        lineChart = setupLineChart(xLabels, auslastungList.toArray(new Double[0]), startDate, endDate, ignoreWeekends);
        lineChart.setHeight("400px");
        mainLayout.add(lineChart);
    }


    /**
     * Aktualisiert das Tortendiagramm basierend auf den gegebenen Parametern.
     *
     * @param raumAuslastungen       Eine Liste von RaumAuslastung-Objekten, die die Auslastungsdaten enthalten.
     * @param totalAuslastungInPercent Die gesamte Auslastung in Prozent, die zur Skalierung der Veranstaltungsauslastung verwendet wird.
     * @param startDate              Das Startdatum des Zeitraums, für den die Auslastung berechnet wird.
     * @param endDate                Das Enddatum des Zeitraums, für den die Auslastung berechnet wird.
     * @param ignoreWeekends         Gibt an ob Wochenenden ausgeschlossen werden sollen.
     */
    private void updatePieChart(List<RaumAuslastung> raumAuslastungen, double totalAuslastungInPercent, LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {
        Map<String, Double> eventAuslastung = new HashMap<>();
        double totalVeranstaltungszeit = 0.0;

        // Durchlaufe den Zeitraum von startDate bis endDate
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Iteration über alle RaumAuslastungen
            for (RaumAuslastung raumAuslastung : raumAuslastungen) {
                // Hole alle Buchungen für den aktuellen Tag und Raum
                Set<Buchung> buchungen = buchungService.findAllByDateAndRoom(currentDate, raumAuslastung.raum);

                // Iteration über alle Buchungen
                for (Buchung buchung : buchungen) {
                    // Datum der aktuellen Buchung
                    LocalDate buchungDate = buchung.getDate();

                    // Überprüfe, ob Wochenenden ausgeschlossen werden sollen und ob die Buchung an einem Wochenende stattfindet
                    if (ignoreWeekends && (buchungDate.getDayOfWeek() == DayOfWeek.SATURDAY || buchungDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                        continue; // Überspringe die Buchung, wenn sie an einem Wochenende ist und Wochenenden ausgeschlossen werden sollen
                    }

                    // Hole die Bezeichnung der Veranstaltung aus der Buchung
                    String veranstaltung = buchung.getVeranstaltung().getBezeichnung();

                    // Berechne die tägliche Auslastung für das Buchungsdatum
                    double tagesAuslastung = raumAuslastung.getDailyAuslastung(buchungDate);

                    // Addiere die tägliche Auslastung zur bestehenden Auslastung der Veranstaltung
                    eventAuslastung.put(veranstaltung, eventAuslastung.getOrDefault(veranstaltung, 0.0) + tagesAuslastung);

                    // Addiere die tägliche Auslastung zur gesamten Veranstaltungszeit
                    totalVeranstaltungszeit += tagesAuslastung;
                }
            }
            // Erhöhe das aktuelle Datum um einen Tag
            currentDate = currentDate.plusDays(1);
        }

        if (totalVeranstaltungszeit == 0) {
            // Keine Veranstaltungen in dem Zeitraum
            totalVeranstaltungszeit = 1.0; // Vermeiden von Division durch Null
        }

        // Skalierung der Werte
        double skalierungsFaktor = totalAuslastungInPercent / totalVeranstaltungszeit;

        // Skaliert die Werte der Veranstaltungen
        Map<String, Double> skalierteEventAuslastung = new HashMap<>();
        for (Map.Entry<String, Double> entry : eventAuslastung.entrySet()) {
            double skalierterWert = entry.getValue() * skalierungsFaktor;
            skalierteEventAuslastung.put(entry.getKey(), skalierterWert);
        }

        // Berechne die skalierte freie Kapazität
        double freieKapazitaet = 100.0 - totalAuslastungInPercent;
        skalierteEventAuslastung.put("Freie Kapazität", freieKapazitaet);

        // Runden der Werte auf zwei Nachkommastellen
        for (Map.Entry<String, Double> entry : skalierteEventAuslastung.entrySet()) {
            entry.setValue(Math.round(entry.getValue() * 100.0) / 100.0);
        }

        // Entfernen des vorhandenen Diagramms wenn berets ein Tortendiagramm angezeigt wird
        if (chartLayout != null) {
            remove(chartLayout);
        }

        // Erstellen und Hinzufügen des neuen Tortendiagramms
        pieChart = setupPieChart(skalierteEventAuslastung);
        pieChart.setHeight("400px");

        chartLayout = new VerticalLayout(pieChart);
        chartLayout.setAlignItems(Alignment.CENTER);
        chartLayout.setWidthFull();

        chartLayout.add(pieChart);
        add(chartLayout);
    }


    /**
     * Diese Klasse repräsentiert die Auslastung eines Raumes über einen bestimmten Zeitraum.
     */
    private record RaumAuslastung(Raum raum, BuchungService buchungService, LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {

        //gibt den Namen des Raums als String zurück.
        public String raumToString() {
            return raum.toString();
        }

        //gibt die Auslastung des Raums formartiert zurück
        public String getAuslastungAsString() {
            return String.format("%.2f", getAuslastung()) + " %";
        }

         // Berechnet die gesamte Auslastung des Raums über den angegebenen Zeitraum.
         // @return Die gesamte prozentuale Auslastung des Raums im Zeitraum.
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


         // Berechnet die Auslastung des Raums für ein bestimmtes Datum.
         // @param date Das Datum, für das die Auslastung berechnet werden soll.
         // @return Die Auslastung des Raums an dem angegebenen Datum.
        public double getDailyAuslastung(LocalDate date) {
            if (ignoreWeekends && (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                return 0;
            }
            return calculateAuslastung(buchungService.findAllByDateAndRoom(date, raum));
        }


         // Berechnet die Auslastung basierend auf der Anzahl an Buchungen für einen bestimmten Tag.
         // @param buchungen Eine Menge von Buchungen für den Tag.
         // @return Die Auslastung als Prozentsatz.
        private double calculateAuslastung(Set<Buchung> buchungen) {
            return buchungen.size() / 7.0 * 100;
        }
    }
}

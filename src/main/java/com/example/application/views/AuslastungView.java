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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author marcel weithoener
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

    /**
     * Konstruktor der Klasse KapaView
     *
     * @param buchungService Service für Buchungen
     * @param raumService    Service für Räume
     */
    public AuslastungView(BuchungService buchungService, RaumService raumService) {
        this.raumService = raumService;
        this.buchungService = buchungService;

        setupGrid(raumService);
        setupInteractionBar();

    }

    /**
     * Erstellt ein Grid für die Raumauslastung
     *
     * @param raumService Service für Räume
     */
    private void setupGrid(RaumService raumService) {
        grid = new Grid<>();
        grid.setAllRowsVisible(true);

        grid.addColumn(RaumAuslastung::raumToString)
                .setKey("Raum")
                .setHeader("Raum")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setSortable(true)
                .setFooter("Anzahl Räume " + (long) raumService.findAll().size());
        grid.addColumn(RaumAuslastung::getAuslastungAsString)
                .setKey("Auslastung")
                .setHeader("Auslastung")
                .setFlexGrow(0)
                .setAutoWidth(true)
                .setSortable(true);

        updateGrid(LocalDate.now(), LocalDate.now(), false);
    }

    /**
     * Erstellt die Interaktionsleiste für die Raumauslastung
     */
    private void setupInteractionBar() {
        startDatePicker.setValue(LocalDate.now());
        startDatePicker.addValueChangeListener(event -> updateGrid(event.getValue(), endDatePicker.getValue(), weeekendCheckbox.getValue()));

        endDatePicker.setValue(LocalDate.now());
        endDatePicker.addValueChangeListener(event -> updateGrid(startDatePicker.getValue(), event.getValue(), weeekendCheckbox.getValue()));

        HorizontalLayout dateLayout = new HorizontalLayout();

        weeekendCheckbox.addValueChangeListener(event -> updateGrid(startDatePicker.getValue(), endDatePicker.getValue(), weeekendCheckbox.getValue()));

        dateLayout.add(startDatePicker, endDatePicker, weeekendCheckbox);
        dateLayout.setAlignItems(Alignment.BASELINE);
        add(dateLayout);
        add(grid);
    }

    /**
     * Aktualisiert das Grid für die Raumauslastung
     *
     * @param startDate      Startdatum
     * @param endDate        Enddatum
     * @param ignoreWeekends Wochenenden ignorieren
     */
    private void updateGrid(LocalDate startDate, LocalDate endDate, boolean ignoreWeekends) {
        Set<Raum> raeume = raumService.findAll();
        List<RaumAuslastung> raumAuslastungen = new ArrayList<>();
        for (Raum raum : raeume) {
            raumAuslastungen.add(new RaumAuslastung(raum, buchungService, startDate, endDate, ignoreWeekends));
        }
        double totalAuslastung = 0;
        for (RaumAuslastung raumAuslastung : raumAuslastungen) {
            totalAuslastung += raumAuslastung.getAuslastung();
        }
        grid.getColumnByKey("Auslastung").setFooter("Gesamtauslastung: " + String.format("%.2f", totalAuslastung / raumAuslastungen.size()) + " %");
        grid.setItems(raumAuslastungen);
    }

    /**
     * Record für die Raumauslastung
     * Records wurden in Java 14 eingeführt und sind eine spezielle Art von Klasse, die nur Daten speichern kann.
     * Sie sind unveränderlich, was bedeutet, dass ihre Felder nach der Initialisierung nicht mehr geändert werden können.
     * Hier wird ein Record verwendet, um die Raumauslastung zu berechnen und in der Grid anzuzeigen.
     *
     * @param raum
     * @param buchungService
     * @param startDate
     * @param endDate
     * @param ignoreWeekends
     */
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
            while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                if (ignoreWeekends && (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
                    currentDate = currentDate.plusDays(1);
                    continue;
                }
                auslastung += calculateAuslastung(buchungService.findAllbyDateAndRoom(currentDate, raum));
                currentDate = currentDate.plusDays(1);
                daycount++;
            }
            return auslastung / daycount;
        }

        private double calculateAuslastung(Set<Buchung> buchungen) {
            return buchungen.size() / 7.0 * 100;
        }
    }
}

package com.example.application.dialogs;


import com.example.application.data.entities.Raum;
import com.example.application.data.enums.Zeitslot;
import com.example.application.services.BuchungService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BelegungWocheDialog extends Dialog {

    private final BuchungService buchungService;
    private final Raum selectedRaum;
    private final H3 kwTitle = new H3();
    private final VerticalLayout verticalLayout;
    private int kw;


    public BelegungWocheDialog(Raum selectedRaum, BuchungService buchungService) {
        this.buchungService = buchungService;
        this.selectedRaum = selectedRaum;

        Calendar cal = Calendar.getInstance();
        kw = cal.get(Calendar.WEEK_OF_YEAR);

        verticalLayout = new VerticalLayout();
        setWidth("50vw");
        setHeight("60vh");
        HorizontalLayout header = new HorizontalLayout();
        Button previousKWButton = new Button("Vorherige KW");
        Button nextKWButton = new Button("Nächste KW");
        header.setAlignItems(FlexComponent.Alignment.BASELINE);
        updateHeader();
        header.add(previousKWButton, kwTitle, nextKWButton);

        verticalLayout.add(header, createGrid());
        add(verticalLayout);

        previousKWButton.addClickListener(event -> {
            kw = kw - 1;
            updateHeader();
            updateGrid();
        });
        nextKWButton.addClickListener(event -> {
            kw = kw + 1;
            updateHeader();
            updateGrid();
        });
    }

    private void updateHeader() {
        LocalDate firstDate = LocalDate.now().withDayOfYear(1).plusWeeks(kw - 1);
        LocalDate lastDate = firstDate.plusDays(6);
        String firstDateFormatted = firstDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String lastDateFormatted = lastDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        kwTitle.setText("KW " + kw + " (" + firstDateFormatted + " - " + lastDateFormatted + ")");
    }

    private void updateGrid() {
        verticalLayout.remove(verticalLayout.getComponentAt(1));
        verticalLayout.add(createGrid());
    }

    private Grid<KWAuslastung> createGrid() {
        Grid<KWAuslastung> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(KWAuslastung::getZeitslot).setHeader("Zeitslot").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isMontagBelegt())).setHeader("Montag").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isDienstagBelegt())).setHeader("Dienstag").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isMittwochBelegt())).setHeader("Mittwoch").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isDonnerstagBelegt())).setHeader("Donnerstag").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isFreitagBelegt())).setHeader("Freitag").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isSamstagBelegt())).setHeader("Samstag").setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(kwAuslastung -> createStatusIcon(kwAuslastung.isSonntagBelegt())).setHeader("Sonntag").setAutoWidth(true);

        LocalDate kwStart = LocalDate.now().withDayOfYear(1).plusWeeks(kw - 1);

        List<KWAuslastung> auslastungen = new ArrayList<>();
        for (Zeitslot zeitslot : Zeitslot.values()) {
            KWAuslastung auslastung = new KWAuslastung(zeitslot);
            auslastung.setMontagBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart));
            auslastung.setDienstagBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart.plusDays(1)));
            auslastung.setMittwochBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart.plusDays(2)));
            auslastung.setDonnerstagBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart.plusDays(3)));
            auslastung.setFreitagBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart.plusDays(4)));
            auslastung.setSamstagBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart.plusDays(5)));
            auslastung.setSonntagBelegt(buchungService.roomBooked(selectedRaum, zeitslot, kwStart.plusDays(6)));
            auslastungen.add(auslastung);
        }
        grid.setItems(auslastungen);
        grid.setAllRowsVisible(true);
        return grid;
    }

    private Icon createStatusIcon(boolean belegt) {
        Icon icon;
        if (belegt) {
            icon = new Icon("vaadin", "close");
            icon.getElement().getThemeList().add("badge error");
            icon.setTooltipText("Belegt");
        } else {
            icon = new Icon("vaadin", "check-circle");
            icon.getElement().getThemeList().add("badge success");
            icon.setTooltipText("Verfügbar");
        }
        icon.getStyle().set("padding", "var(--lumo-space-xs)");
        return icon;
    }

    /**
     * Hilfsklasse zur Darstellung der Auslastung eines Raumes in einer Kalenderwoche
     *
     * @author Marcel Weithoener
     */
    private static class KWAuslastung {

        private final Zeitslot zeitslot;
        private boolean montagBelegt;
        private boolean dienstagBelegt;
        private boolean mittwochBelegt;
        private boolean donnerstagBelegt;
        private boolean freitagBelegt;
        private boolean samstagBelegt;
        private boolean sonntagBelegt;

        public KWAuslastung(Zeitslot zeitslot) {
            this.zeitslot = zeitslot;
        }

        public Zeitslot getZeitslot() {
            return zeitslot;
        }

        public boolean isMontagBelegt() {
            return montagBelegt;
        }

        public void setMontagBelegt(boolean montagBelegt) {
            this.montagBelegt = montagBelegt;
        }

        public boolean isDienstagBelegt() {
            return dienstagBelegt;
        }

        public void setDienstagBelegt(boolean dienstagBelegt) {
            this.dienstagBelegt = dienstagBelegt;
        }

        public boolean isMittwochBelegt() {
            return mittwochBelegt;
        }

        public void setMittwochBelegt(boolean mittwochBelegt) {
            this.mittwochBelegt = mittwochBelegt;
        }

        public boolean isDonnerstagBelegt() {
            return donnerstagBelegt;
        }

        public void setDonnerstagBelegt(boolean donnerstagBelegt) {
            this.donnerstagBelegt = donnerstagBelegt;
        }

        public boolean isFreitagBelegt() {
            return freitagBelegt;
        }

        public void setFreitagBelegt(boolean freitagBelegt) {
            this.freitagBelegt = freitagBelegt;
        }

        public boolean isSamstagBelegt() {
            return samstagBelegt;
        }

        public void setSamstagBelegt(boolean samstagBelegt) {
            this.samstagBelegt = samstagBelegt;
        }

        public boolean isSonntagBelegt() {
            return sonntagBelegt;
        }

        public void setSonntagBelegt(boolean sonntagBelegt) {
            this.sonntagBelegt = sonntagBelegt;
        }
    }

}

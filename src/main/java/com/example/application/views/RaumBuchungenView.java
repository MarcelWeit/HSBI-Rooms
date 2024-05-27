package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Raum;
import com.example.application.services.BuchungService;
import com.example.application.services.RaumService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.function.Consumer;

@Route(value = "buchungen-raum", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
@PageTitle("Raumbuchungen")
public class RaumBuchungenView extends VerticalLayout {

    private final BuchungService buchungService;
    private final Grid<Buchung> raumBuchungGrid = new Grid<>(Buchung.class, false);
    private final RaumService roomService;

    public RaumBuchungenView(BuchungService buchungService, RaumService roomService) {
        this.buchungService = buchungService;
        this.roomService = roomService;
        setupGrid();
        add(raumBuchungGrid);
    }

    private void setupGrid() {

        GridListDataView<Buchung> dataView = raumBuchungGrid.setItems(buchungService.findAll());
        raumBuchungGrid.addColumn(Buchung::getRoom).setHeader("Raumnummer").setKey("refNr");
        raumBuchungGrid.addColumn(Buchung::getVeranstaltung).setHeader("Veranstaltung").setKey("veranstaltung");
        raumBuchungGrid.addColumn(Buchung::getDozent).setHeader("Dozent").setKey("dozent");
        raumBuchungGrid.addColumn(Buchung::getDate).setHeader("Datum").setKey("date");
        raumBuchungGrid.addColumn(Buchung::getStartZeit).setHeader("StartZeit").setKey("startZeit");
        raumBuchungGrid.addColumn(Buchung::getEndZeit).setHeader("EndZeit").setKey("endZeit");

        raumBuchungGrid.getColumnByKey("refNr").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("veranstaltung").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("dozent").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("date").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("startZeit").setAutoWidth(true).setFlexGrow(0);
        raumBuchungGrid.getColumnByKey("endZeit").setAutoWidth(true).setFlexGrow(0);

        GridSortOrder<Buchung> sortOrderDate = new GridSortOrder<>(raumBuchungGrid.getColumnByKey("date"), SortDirection.ASCENDING);
        GridSortOrder<Buchung> sortOrderTime = new GridSortOrder<>(raumBuchungGrid.getColumnByKey("startZeit"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<Buchung>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrderDate);
        sortOrders.add(sortOrderTime);
        raumBuchungGrid.sort(sortOrders);

        //        setupFilter(dataView);
    }

    private void setupFilter(GridListDataView<Buchung> dataView) {
        BuchungFilter buchungFilter = new BuchungFilter(dataView);

        raumBuchungGrid.getHeaderRows().clear();
        HeaderRow headerRow = raumBuchungGrid.appendHeaderRow();

        Consumer<Raum> roomFilterChangeConsumer = buchungFilter::setRoom;
        ComboBox<Raum> roomComboBox = new ComboBox<>();
        roomComboBox.setItems(roomService.findAll());
        roomComboBox.setClearButtonVisible(true);
        roomComboBox.addValueChangeListener(e -> roomFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(raumBuchungGrid.getColumnByKey("refNr")).setComponent(roomComboBox);

    }

    private static class BuchungFilter {
        private final GridListDataView<Buchung> dataView;
        private Raum room;
        private LocalDate date;
        private LocalTime startZeit;
        private LocalTime endZeit;

        public BuchungFilter(GridListDataView<Buchung> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setRoom(Raum room) {
            this.room = room;
            this.dataView.refreshAll();
        }

        public void setDate(LocalDate date) {
            this.date = date;
            this.dataView.refreshAll();
        }

        public void setStartZeit(LocalTime startZeit) {
            this.startZeit = startZeit;
            this.dataView.refreshAll();
        }

        public void setEndZeit(LocalTime endZeit) {
            this.endZeit = endZeit;
            this.dataView.refreshAll();
        }

        public boolean test(Buchung buchung) {
            boolean matchesRefNr = matches(buchung.getRoom().getRefNr(), room.getRefNr());
            boolean matchesDate = matches(buchung.getDate().toString(), date.toString());
            boolean matchesStartZeit = matches(buchung.getStartZeit().toString(), startZeit.toString());
            boolean matchesEndZeit = matches(buchung.getEndZeit().toString(), endZeit.toString());
            return matchesRefNr && matchesDate && matchesStartZeit && matchesEndZeit;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }

}

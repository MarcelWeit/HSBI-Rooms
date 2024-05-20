package com.example.application.views;

import com.example.application.data.entities.Room;
import com.example.application.services.AusstattungService;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

@Route(value = "room-verwaltung", layout = MainLayout.class)
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@PageTitle("Räume verwalten")
public class RaumVerwaltung extends VerticalLayout {

    private final AusstattungService ausstattungService;
    private final RoomService roomService;

    private final Grid<Room> roomGrid = new Grid<>(Room.class);

    public RaumVerwaltung(AusstattungService ausstattungService, RoomService roomService) {
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;

        setupGrid();
    }

    private void setupGrid() {
        roomGrid.setItems(roomService.findAll());
        roomGrid.removeColumn(roomGrid.getColumnByKey("id"));

        roomGrid.setColumnOrder(roomGrid.getColumnByKey("refNr"),
                roomGrid.getColumnByKey("fachbereich"),
                roomGrid.getColumnByKey("position"),
                roomGrid.getColumnByKey("typ"),
                roomGrid.getColumnByKey("capacity"),
                roomGrid.getColumnByKey("ausstattung"));
        add(roomGrid);
    }
}

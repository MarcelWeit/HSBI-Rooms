package com.example.application.views;

import com.example.application.data.entities.Room;
import com.example.application.data.entities.Ausstattung;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Räume anzeigen")
@Route(value = "show-rooms", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ShowRoomsView extends VerticalLayout {

        private final RoomService roomService;

        public ShowRoomsView(RoomService roomService) {
            addClassNames("show-rooms");
            this.roomService = roomService;
            createComponents();
        }

        @Transactional
        public void createComponents(){
            Grid<Room> grid = new Grid<>();
            Set<Room> rooms = roomService.findAll();
            rooms.forEach(room -> Hibernate.initialize(room.getAusstattung()));
            grid.setItems(rooms);
            grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

            grid.addColumn(Room::getRefNr).setHeader("Ref Nr");
            grid.addColumn(Room::getTyp).setHeader("Typ");
            grid.addColumn(Room::getCapacity).setHeader("Kapazität");
            grid.addColumn(room -> room.getAusstattung().stream()
                            .map(Ausstattung::getBez)
                            .collect(Collectors.joining(", ")))
                    .setHeader("Ausstattung");
            grid.addColumn(Room::getFachbereich).setHeader("Fachbereich");

            add(grid);
        }
}

package com.example.application.views;

import com.example.application.data.entities.Room;
import com.example.application.data.entities.Ausstattung;
import com.example.application.services.AusstattungService;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import org.hibernate.Hibernate;

import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Räume")
@Route(value = "show-rooms", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class RoomManagement extends VerticalLayout {

        private final RoomService roomService;
        private final AusstattungService ausstattungService;

        private Grid<Room> grid;

        public RoomManagement(RoomService roomService, AusstattungService ausstattungService) {
            addClassNames("show-rooms");
            this.roomService = roomService;
            this.ausstattungService = ausstattungService;
            createComponents();
        }

        public void createComponents(){
            Button addRoomButton = new Button("Raum erstellen");
            addRoomButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            addRoomButton.addClickListener(e ->
                createDialog()
            );

            createGrid();
            add(addRoomButton, grid);
        }

        private void createGrid(){
            grid = new Grid<>();
            grid.setAllRowsVisible(true);
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
        }

        private void createDialog(){
            Dialog addRoomDialog = new Dialog();
            addRoomDialog.setHeaderTitle("Raum erstellen");

            Binder<Room> binder = new Binder<>(Room.class);
            TextField refNr = new TextField("ReferenzBezeichnung");
            refNr.setRequired(true);
            binder.forField(refNr)
                    .withValidator(refNrValue -> refNrValue.matches("^[a-zA-Z]{1}.{0,3}$"),
                            "ReferenzBezeichnung muss mit einem Buchstaben anfangen und darf maximal 4 Zeichen lang sein")
                    .bind(Room::getRefNr, Room::setRefNr);

            IntegerField kapa = new IntegerField("Kapazität");
            kapa.setErrorMessage("Bitte geben Sie eine Zahl ein");
            kapa.setValue(30);
            kapa.setStepButtonsVisible(true);
            kapa.setMin(1);
            kapa.setMax(1000);
            binder.forField(kapa)
                    .bind(Room::getCapacity, Room::setCapacity);

            MultiSelectComboBox<Ausstattung> ausstattung = new MultiSelectComboBox<>("Ausstattung");
            ausstattung.setItems(ausstattungService.findAll());
            ausstattung.setItemLabelGenerator(Ausstattung::getBez);
            binder.forField(ausstattung)
                    .bind(Room::getAusstattung, Room::setAusstattung);


            ComboBox<String> typ = new ComboBox<>("Raumtyp");
            typ.setRequired(true);
            typ.setItems("Hörsaal", "Seminarraum", "Rechnerraum", "Besprechungsraum");
            binder.forField(typ)
                    .bind(Room::getTyp, Room::setTyp);

            ComboBox<String> fachbereich = new ComboBox<>("Fachbereich");
            fachbereich.setRequired(true);
            fachbereich.setItems("Wirtschaft", "Gestaltung", "Sozialwesen", "Ingenieurwissenschaften und Mathematik", "Gesundheit", "Campus Minden", "Campus Gütersloh");
            binder.forField(fachbereich)
                    .bind(Room::getFachbereich, Room::setFachbereich);

            VerticalLayout dialogLayout = new VerticalLayout(refNr, ausstattung, kapa, typ, fachbereich);
            addRoomDialog.add(dialogLayout);

            Button createButton = new Button("Erstellen");
            createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            createButton.addClickShortcut(Key.ENTER);
            createButton.addClickListener(ev -> {
                if (refNr.isEmpty() || fachbereich.isEmpty() || typ.isEmpty()){
                    Notification.show("Bitte alle Pflichtfelder befüllen", 2000, Notification.Position.MIDDLE);
                } else {
                    Room room = new Room();
                    if (binder.writeBeanIfValid(room)) {
                        // If the validation passes, the room object will be updated with the field values
                        if (roomService.existsById(room.getRefNr())) {
                            Notification.show("Raum existiert bereits", 2000, Notification.Position.MIDDLE);
                        } else {
                            roomService.update(room);
                            addRoomDialog.close();
                            refNr.clear();
                            refNr.setErrorMessage("");
                            refNr.setInvalid(false);
                            typ.clear();
                            fachbereich.clear();
                            ausstattung.clear();
                            Notification.show("Raum erstellt", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                            grid.setItems(roomService.findAll());
                        }
                    } else {
                        Notification.show("Bitte alle Pflichtfelder befüllen", 2000, Notification.Position.MIDDLE);
                    }
                }
            });

            Button cancelButton = new Button("Abbrechen", f -> addRoomDialog.close());

            addRoomDialog.getFooter().add(createButton);
            addRoomDialog.getFooter().add(cancelButton);

            addRoomDialog.open();
        }
}

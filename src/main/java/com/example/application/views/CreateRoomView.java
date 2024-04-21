package com.example.application.views;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Room;
import com.example.application.services.AusstattungService;
import com.example.application.services.RoomService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/**
 * A view for creating a room.
 *
 * @author marcel weithoener
 * @version 1.0
 *
 */

@PageTitle("CreateRoom")
@Route(value = "create-room", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class CreateRoomView extends VerticalLayout {

    private final RoomService roomService;
    private final AusstattungService ausstattungService;

    public CreateRoomView(RoomService roomService, AusstattungService ausstattungService) {
        addClassNames("create-room");
        this.roomService = roomService;
        this.ausstattungService = ausstattungService;
        createComponents();
    }

    private void createComponents() {

        IntegerField kapa = new IntegerField("Kapazität");
        kapa.setErrorMessage("Bitte geben Sie eine Zahl ein");
        kapa.setValue(30);
        kapa.setStepButtonsVisible(true);
        kapa.setMin(1);
        kapa.setMax(1000);

        TextField name = new TextField("Name");
        IntegerField refNr = new IntegerField("Referenznummer");
        TextField location = new TextField("Standort");
        MultiSelectComboBox<Ausstattung> ausstattung = new MultiSelectComboBox<>("Ausstattung");
        ausstattung.setItems(ausstattungService.findAll());
        ausstattung.setItemLabelGenerator(Ausstattung::getBez);

        ComboBox<String> typ = new ComboBox<>("Raumtyp");
        typ.setItems("Hörsaal", "Seminarraum", "Rechnerraum", "Besprechungsraum");

        ComboBox<String> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems("Wirtschaft", "Gestaltung", "Sozialwesen", "Ingenieurwissenschaften und Mathematik", "Gesundheit", "Campus Minden", "Campus Gütersloh");

        Button create = new Button("Create", new Icon("lumo", "plus"));
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.addClickShortcut(Key.ENTER);

        add(refNr, ausstattung, kapa, location, typ, fachbereich, create);

        create.addClickListener(e -> {
            if (location.isEmpty() || refNr.isEmpty() || fachbereich.isEmpty() || typ.isEmpty()){
                Notification.show("Bitte alle Pflichtfelder befüllen", 2000, Notification.Position.MIDDLE);
            } else {
                long refNrValue = refNr.getValue();
                if (roomService.existsById(refNrValue)) {
                    Notification.show("Referenznummer existiert bereits", 2000, Notification.Position.MIDDLE);
                } else {
                    roomService.update(new Room(kapa.getValue(), location.getValue(), ausstattung.getSelectedItems(), refNr.getValue(), typ.getValue(), fachbereich.getValue()));
                    kapa.clear();
                    location.clear();
                    refNr.clear();
                    typ.clear();
                    fachbereich.clear();
                    ausstattung.clear();
                }
            }
        });
    }
}

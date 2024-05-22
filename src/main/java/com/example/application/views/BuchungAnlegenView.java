package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Room;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.RoomService;
import com.example.application.services.VeranstaltungService;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

@Route(value = "create-buchung", layout = MainLayout.class)
@PageTitle("Buchung anlegen")
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
public class BuchungAnlegenView extends Dialog {

    private final RoomService roomService;
    private final DozentService dozentService;
    private final BuchungService buchungService;
    private final VeranstaltungService veranstaltungService;

    private final Binder<Buchung> binder = new Binder<>(Buchung.class);
    private final ComboBox<Room> raum = new ComboBox<>("Raumnummer");
    private final ComboBox<Veranstaltung> veranstaltung = new ComboBox<>("Veranstaltung");
    private final ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
    private final DatePicker date = new DatePicker("Datum");
    private final TimePicker startZeit = new TimePicker("Startzeit");
    private final TimePicker endZeit = new TimePicker("Endzeit");
    private final Button save = new Button("Speichern");
    private final Button cancel = new Button("Abbrechen");
//    ComboBox<Wiederholungsintervall> wiederholungsintervall = new ComboBox<>("Wiederholungsintervall");

    private final Optional<Room> selectedRoom;

    public BuchungAnlegenView(Optional<Room> selectedRoom, RoomService roomService, DozentService dozentService, BuchungService buchungService, VeranstaltungService veranstaltungService) {
        this.roomService = roomService;
        this.dozentService = dozentService;
        this.buchungService = buchungService;
        this.veranstaltungService = veranstaltungService;
        this.selectedRoom = selectedRoom;
        add(createInputLayout());
        createButtonLayout();
    }

    private FormLayout createInputLayout() {
        FormLayout dialogLayout = new FormLayout();

        raum.setItems(roomService.findAll());
        raum.setItemLabelGenerator(Room::getRefNr);
        raum.setRequiredIndicatorVisible(true);
        raum.addValueChangeListener((value -> {
            startZeit.setEnabled(true);
            endZeit.setEnabled(true);
        }));

        veranstaltung.setItems(veranstaltungService.findAll());
        veranstaltung.setItemLabelGenerator(Veranstaltung::getBezeichnung);
        veranstaltung.setRequiredIndicatorVisible(true);

        dozent.setItems(dozentService.findAll());
        dozent.setItemLabelGenerator(Dozent::getNachname);
        dozent.setRequiredIndicatorVisible(true);

        date.setLabel("Datum");
        date.setRequiredIndicatorVisible(true);

        startZeit.setLabel("Startzeit");
        startZeit.setRequiredIndicatorVisible(true);
        startZeit.setMin(LocalTime.of(8, 0));
        startZeit.setMax(LocalTime.of(22, 0));
        startZeit.setStep(Duration.ofMinutes(15));
        startZeit.addValueChangeListener(e -> {
            if (startZeit.getValue() != null) {
                endZeit.setMin(startZeit.getValue().plus(Duration.ofMinutes(15)));
            }
            if (endZeit.getValue() != null) {
                checkIfBelegt();
            }
        });
        startZeit.setEnabled(false);

        endZeit.setLabel("Endzeit");
        endZeit.setRequiredIndicatorVisible(true);
        endZeit.setMin(LocalTime.of(8, 15));
        endZeit.setStep(Duration.ofMinutes(15));
        endZeit.addValueChangeListener(e -> {
            if (startZeit.getValue() != null) {
                checkIfBelegt();
            }
        });
        endZeit.setEnabled(false);

        binder.forField(raum).asRequired("Bitte wählen Sie einem Raum aus").bind(Buchung::getRoom, Buchung::setRoom);
        binder.forField(veranstaltung).asRequired().bind(Buchung::getVeranstaltung, Buchung::setVeranstaltung);
        binder.forField(dozent).asRequired().bind(Buchung::getDozent, Buchung::setDozent);
        binder.forField(date).asRequired().bind(Buchung::getDate, Buchung::setDate);
        binder.forField(startZeit).asRequired().bind(Buchung::getStartZeit, Buchung::setStartZeit);
        binder.forField(endZeit).asRequired().bind(Buchung::getEndZeit, Buchung::setEndZeit);

        if (selectedRoom.isPresent()) {
            raum.setValue(selectedRoom.get());
            raum.setEnabled(false);
            startZeit.setEnabled(true);
            endZeit.setEnabled(true);
        }

        dialogLayout.add(raum, date, veranstaltung, dozent, startZeit, endZeit);
        dialogLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2));
        dialogLayout.setColspan(veranstaltung, 2);
        dialogLayout.setColspan(dozent, 2);
        // @ToDo nötig?
        dialogLayout.setMaxWidth("25vw");

        return dialogLayout;

    }

    private void checkIfBelegt() {
        if (startZeit.getValue() != null && raum.getValue() != null && date.getValue() != null && endZeit.getValue() != null) {
            if (buchungService.roomBooked(raum.getValue(), startZeit.getValue(), endZeit.getValue(), date.getValue())) {
                Notification.show("Raum bereits belegt", 4000, Notification.Position.MIDDLE);
                save.setEnabled(false);
            } else {
                save.setEnabled(true);
            }
        }
    }

    private void createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            if (validateAndSave()) {
                close();
            }
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> close());
        this.getFooter().add(save, cancel);
    }

    private boolean validateAndSave() {
        Buchung newBuchung = new Buchung();
        if (binder.writeBeanIfValid(newBuchung)) {
            buchungService.save(newBuchung);
            Notification sucessNotification = Notification.show("Erfolgreich gespeichert", 4000, Notification.Position.MIDDLE);
            sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            binder.getFields().forEach(HasValue::clear);
            startZeit.setEnabled(false);
            endZeit.setEnabled(false);
            return true;
        } else {
            Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
            return false;
        }

    }
}

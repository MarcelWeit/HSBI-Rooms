package com.example.application.dialogs;

import com.example.application.data.entities.*;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.RaumService;
import com.example.application.services.VeranstaltungService;
import com.example.application.views.MainLayout;
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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Route(value = "create-buchung", layout = MainLayout.class)
@PageTitle("Buchung anlegen")
@RolesAllowed({"ADMIN", "DOZENT", "FBPLANUNG"})
public class BuchungAnlegenDialog extends Dialog {

    private final RaumService roomService;
    private final DozentService dozentService;
    private final BuchungService buchungService;
    private final VeranstaltungService veranstaltungService;

    private final Binder<Buchung> binder = new Binder<>(Buchung.class);
    private final ComboBox<Raum> raum = new ComboBox<>("Raumnummer");
    private final ComboBox<Veranstaltung> veranstaltung = new ComboBox<>("Veranstaltung");
    private final ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
    private final DatePicker date = new DatePicker("Datum");
    private final TimePicker startZeit = new TimePicker("Startzeit");
    private final TimePicker endZeit = new TimePicker("Endzeit");
    private final Button save = new Button("Speichern");
    private final Button cancel = new Button("Abbrechen");
    private final RadioButtonGroup<Wiederholungsintervall> wiederholungsintervall = new RadioButtonGroup<>("Wiederholungsintervall");
    private final DatePicker endDatum = new DatePicker("Letzter Buchungstag");

    private final Optional<Buchung> selectedBuchung;
    private final Optional<Raum> selectedRoom;
    private final Optional<Veranstaltung> selectedVeranstaltung;
    private final Optional<Dozent> selectedDozent;

    public BuchungAnlegenDialog(Optional<Buchung> selectedBuchung, Optional<Raum> selectedRoom, Optional<Veranstaltung> selectedVeranstaltung, Optional<Dozent> selectedDozent, RaumService roomService, DozentService dozentService, BuchungService buchungService, VeranstaltungService veranstaltungService) {
        this.roomService = roomService;
        this.dozentService = dozentService;
        this.buchungService = buchungService;
        this.veranstaltungService = veranstaltungService;
        this.selectedBuchung = selectedBuchung;
        this.selectedRoom = selectedRoom;
        this.selectedVeranstaltung = selectedVeranstaltung;
        this.selectedDozent = selectedDozent;
        add(createInputLayout());
        createButtonLayout();
    }

    private FormLayout createInputLayout() {
        FormLayout dialogLayout = new FormLayout();

        raum.setItems(roomService.findAll());
        raum.setItemLabelGenerator(Raum::getRefNr);
        raum.setRequiredIndicatorVisible(true);
        raum.addValueChangeListener((value -> {
            startZeit.setEnabled(true);
            endZeit.setEnabled(true);
        }));

        veranstaltung.setItems(veranstaltungService.findAll());
        veranstaltung.setItemLabelGenerator(Veranstaltung::getBezeichnung);
        veranstaltung.setRequiredIndicatorVisible(true);

        dozent.setItems(dozentService.findAll());
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
            if (endZeit.getValue() != null && startZeit.getValue() != null) {
                if (!(selectedBuchung.isPresent() && startZeit.getValue().equals(selectedBuchung.get().getStartZeit()) && endZeit.getValue().equals(selectedBuchung.get().getEndZeit()))) {
                    checkIfBelegt();
                }
            }
        });
        startZeit.setEnabled(false);

        endZeit.setLabel("Endzeit");
        endZeit.setRequiredIndicatorVisible(true);
        endZeit.setMin(LocalTime.of(8, 15));
        endZeit.setStep(Duration.ofMinutes(15));
        endZeit.addValueChangeListener(e -> {
            if (startZeit.getValue() != null && endZeit.getValue() != null) {
                if (!(selectedBuchung.isPresent() && startZeit.getValue().equals(selectedBuchung.get().getStartZeit()) && endZeit.getValue().equals(selectedBuchung.get().getEndZeit()))) {
                    checkIfBelegt();
                }
            }
        });
        endZeit.setEnabled(false);

        wiederholungsintervall.setItems(Wiederholungsintervall.values());
        wiederholungsintervall.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        wiederholungsintervall.setValue(Wiederholungsintervall.EINMALIG);
        endDatum.setVisible(false);
        wiederholungsintervall.addValueChangeListener(e -> {
            if (e.getValue().equals(Wiederholungsintervall.EINMALIG)) {
                endDatum.setVisible(false);
            } else {
                endDatum.setVisible(true);
                binder.forField(endDatum).asRequired();
            }
        });

        binder.forField(raum).asRequired("Bitte wählen Sie einem Raum aus").bind(Buchung::getRoom, Buchung::setRoom);
        binder.forField(veranstaltung).asRequired().bind(Buchung::getVeranstaltung, Buchung::setVeranstaltung);
        binder.forField(dozent).asRequired().bind(Buchung::getDozent, Buchung::setDozent);
        binder.forField(date).asRequired().bind(Buchung::getDate, Buchung::setDate);
        binder.forField(startZeit).asRequired().bind(Buchung::getStartZeit, Buchung::setStartZeit);
        binder.forField(endZeit).asRequired().bind(Buchung::getEndZeit, Buchung::setEndZeit);

        if (selectedBuchung.isPresent()) {
            binder.readBean(selectedBuchung.get());
        }
        if (selectedRoom.isPresent()) {
            raum.setValue(selectedRoom.get());
            raum.setEnabled(false);
            startZeit.setEnabled(true);
            endZeit.setEnabled(true);
        }
        if (selectedVeranstaltung.isPresent()) {
            veranstaltung.setValue(selectedVeranstaltung.get());
            veranstaltung.setEnabled(false);
        }
        if (selectedDozent.isPresent()) {
            dozent.setValue(selectedDozent.get());
            dozent.setEnabled(false);
        }

        dialogLayout.add(raum, date, veranstaltung, dozent, startZeit, endZeit);
        // Kein Wiederholungsintervall bei Buchung bearbeiten
        if(selectedBuchung.isEmpty()){
            dialogLayout.add(wiederholungsintervall, endDatum);
        }

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
        if (selectedBuchung.isEmpty()) {
            if(wiederholungsintervall.getValue() == Wiederholungsintervall.EINMALIG) {
                Buchung buchung = new Buchung();
                if (binder.writeBeanIfValid(buchung)) {
                    buchungService.save(buchung);
                    Notification sucessNotification = Notification.show("Buchung gespeichert", 4000, Notification.Position.MIDDLE);
                    sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return true;
                } else {
                    Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
                }
            }
            else if (wiederholungsintervall.getValue() == Wiederholungsintervall.TAEGLICH){
                Buchung startBuchung = new Buchung();
                if (binder.writeBeanIfValid(startBuchung)) {
                    buchungService.save(startBuchung);
                    LocalDate currentDate = startBuchung.getDate();
                    LocalDate endDate = endDatum.getValue();
                    while (currentDate.plusDays(1).isBefore(endDate) || currentDate.isEqual(endDate)) {
                        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

                        // Wochenende wird nicht gebucht
                        if(dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                            Buchung nextBuchung = new Buchung(startBuchung);
                            nextBuchung.setDate(currentDate);
                            buchungService.save(nextBuchung);
                        }
                        currentDate = currentDate.plusDays(1);
                    }
                    Notification sucessNotification = Notification.show("Buchungen erfolgreich gespeichert", 4000, Notification.Position.MIDDLE);
                    sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return true;
                } else {
                    Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
                }
            }
            else if (wiederholungsintervall.getValue() == Wiederholungsintervall.WOECHENTLICH) {
                Buchung startBuchung = new Buchung();
                if (binder.writeBeanIfValid(startBuchung)) {
                    buchungService.save(startBuchung);
                    LocalDate currentDate = startBuchung.getDate();
                    LocalDate endDate = endDatum.getValue();
                    while (currentDate.plusDays(7).isBefore(endDate)) {
                        currentDate = currentDate.plusDays(7);
                        Buchung nextBuchung = new Buchung(startBuchung);
                        nextBuchung.setDate(currentDate);
                        buchungService.save(nextBuchung);
                    }
                    Notification sucessNotification = Notification.show("Buchungen erfolgreich gespeichert", 4000, Notification.Position.MIDDLE);
                    sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return true;
                } else {
                    Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
                }

            }
        } else if (selectedBuchung.isPresent()) {
            Buchung newBuchung = selectedBuchung.get();
            if (binder.writeBeanIfValid(newBuchung)) {
                buchungService.save(newBuchung);
                Notification sucessNotification = Notification.show("Buchung gespeichert", 4000, Notification.Position.MIDDLE);
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
        return false;
    }

}

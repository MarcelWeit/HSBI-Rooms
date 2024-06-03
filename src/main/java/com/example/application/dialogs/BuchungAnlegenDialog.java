package com.example.application.dialogs;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Wiederholungsintervall;
import com.example.application.data.enums.Zeitslot;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.RaumService;
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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.binder.Binder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

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
    private final Button save = new Button("Speichern");
    private final Button cancel = new Button("Abbrechen");
    private final RadioButtonGroup<Wiederholungsintervall> wiederholungsintervall = new RadioButtonGroup<>("Wiederholungsintervall");
    private final DatePicker endDatum = new DatePicker("Letzter Buchungstag");
    private final ComboBox<Zeitslot> zeitslot = new ComboBox<>("Zeitslot");

    private final Optional<Buchung> selectedBuchung;
    private final Optional<Raum> selectedRoom;
    private final Optional<Veranstaltung> selectedVeranstaltung;
    private final Optional<Dozent> selectedDozent;

    private final Optional<RaumBuchungenDialog> previousDialog;

    public BuchungAnlegenDialog(Optional<Buchung> selectedBuchung, Optional<Raum> selectedRoom, Optional<Veranstaltung> selectedVeranstaltung,
                                Optional<Dozent> selectedDozent, RaumService roomService, DozentService dozentService, BuchungService buchungService,
                                VeranstaltungService veranstaltungService, Optional<RaumBuchungenDialog> previousDialog) {
        this.roomService = roomService;
        this.dozentService = dozentService;
        this.buchungService = buchungService;
        this.veranstaltungService = veranstaltungService;
        this.selectedBuchung = selectedBuchung;
        this.selectedRoom = selectedRoom;
        this.selectedVeranstaltung = selectedVeranstaltung;
        this.selectedDozent = selectedDozent;

        this.previousDialog = previousDialog;

        add(createInputLayout());
        createButtonLayout();
    }

    private FormLayout createInputLayout() {
        FormLayout dialogLayout = new FormLayout();

        raum.setItems(roomService.findAll());
        raum.setItemLabelGenerator(Raum::getRefNr);
        raum.setRequiredIndicatorVisible(true);
        raum.addValueChangeListener((value -> zeitslot.setEnabled(true)));

        veranstaltung.setItems(veranstaltungService.findAll());
        veranstaltung.setItemLabelGenerator(Veranstaltung::getBezeichnung);
        veranstaltung.setRequiredIndicatorVisible(true);

        dozent.setItems(dozentService.findAll());
        dozent.setRequiredIndicatorVisible(true);

        date.setLabel("Datum");
        date.setRequiredIndicatorVisible(true);

        zeitslot.setItems(Zeitslot.values());
        zeitslot.setEnabled(false);

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


        if (selectedBuchung.isPresent()) {
            binder.forField(zeitslot).asRequired()
                    .bind(Buchung::getZeitslot, Buchung::setZeitslot);
            binder.readBean(selectedBuchung.get());
        } else {
            binder.forField(zeitslot).asRequired()
                    .withValidator(event -> !buchungService.roomBooked(raum.getValue(), zeitslot.getValue(), date.getValue()), "Raum bereits belegt")
                    .bind(Buchung::getZeitslot, Buchung::setZeitslot);
        }
        if (selectedRoom.isPresent()) {
            raum.setValue(selectedRoom.get());
            raum.setEnabled(false);
            zeitslot.setEnabled(true);
        }
        if (selectedVeranstaltung.isPresent()) {
            veranstaltung.setValue(selectedVeranstaltung.get());
            veranstaltung.setEnabled(false);
        }
        if (selectedDozent.isPresent()) {
            dozent.setValue(selectedDozent.get());
            dozent.setEnabled(false);
        }

        dialogLayout.add(raum, date, veranstaltung, dozent, zeitslot);
        // Kein Wiederholungsintervall bei Buchung bearbeiten
        if (selectedBuchung.isEmpty()) {
            dialogLayout.add(wiederholungsintervall, endDatum);
        }

        dialogLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2));
        dialogLayout.setColspan(veranstaltung, 2);
        dialogLayout.setColspan(dozent, 2);
        dialogLayout.setColspan(zeitslot, 2);
        dialogLayout.setMaxWidth("25vw");

        return dialogLayout;

    }

    //    private void checkIfBelegt() {
    //        if (zeitslot.getValue() != null && raum.getValue() != null && date.getValue() != null) {
    //            if (buchungService.roomBooked(raum.getValue(), zeitslot.getValue(), date.getValue())) {
    //                Notification.show("Raum bereits belegt", 4000, Notification.Position.MIDDLE);
    //                save.setEnabled(false);
    //            } else {
    //                save.setEnabled(true);
    //            }
    //        }
    //    }

    private void createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            if (validateAndSave()) {
                if (previousDialog.isPresent()) {
                    previousDialog.get().updateGrid();
                }
                close();
            }
        });
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> close());
        this.getFooter().add(save, cancel);
    }

    private boolean validateAndSave() {
        Buchung firstBuchung = selectedBuchung.orElseGet(Buchung::new);
        // Erste Buchung wird immer gespeichert, wenn alle Binder erfolgreich
        if (binder.writeBeanIfValid(firstBuchung)) {
            buchungService.save(firstBuchung);
            if (selectedBuchung.isPresent()) {
                binder.getFields().forEach(HasValue::clear);
                zeitslot.setEnabled(false);
            }
            if (wiederholungsintervall.getValue() == Wiederholungsintervall.EINMALIG) {
                Notification sucessNotification = Notification.show("Buchung gespeichert", 4000, Notification.Position.MIDDLE);
                sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                return true;
            }
        } else {
            Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
            return false;
        }
        if (selectedBuchung.isEmpty()) {
            if (wiederholungsintervall.getValue() == Wiederholungsintervall.TAEGLICH) {
                LocalDate currentDate = firstBuchung.getDate();
                LocalDate endDate = endDatum.getValue();
                while (currentDate.plusDays(1).isBefore(endDate) || currentDate.plusDays(1).isEqual(endDate)) {
                    DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                    // Wochenende wird nicht gebucht
                    if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                        Buchung nextBuchung = new Buchung(firstBuchung);
                        nextBuchung.setDate(currentDate);
                        buchungService.save(nextBuchung);
                    }
                    currentDate = currentDate.plusDays(1);
                }
            } else if (wiederholungsintervall.getValue() == Wiederholungsintervall.WOECHENTLICH) {
                LocalDate currentDate = firstBuchung.getDate();
                LocalDate endDate = endDatum.getValue();
                while (currentDate.plusDays(7).isBefore(endDate) || currentDate.plusDays(7).isEqual(endDate)) {
                    currentDate = currentDate.plusDays(7);
                    Buchung nextBuchung = new Buchung(firstBuchung);
                    nextBuchung.setDate(currentDate);
                    buchungService.save(nextBuchung);
                }
            }
            Notification sucessNotification = Notification.show("Buchungen erfolgreich gespeichert", 4000, Notification.Position.MIDDLE);
            sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            return true;
        }
        return false;
    }
}

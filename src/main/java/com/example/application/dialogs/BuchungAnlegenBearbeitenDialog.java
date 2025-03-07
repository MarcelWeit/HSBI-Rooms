package com.example.application.dialogs;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Raum;
import com.example.application.data.entities.Veranstaltung;
import com.example.application.data.enums.Role;
import com.example.application.data.enums.Wiederholungsintervall;
import com.example.application.data.enums.Zeitslot;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.BuchungService;
import com.example.application.services.DozentService;
import com.example.application.services.RaumService;
import com.example.application.services.VeranstaltungService;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Dialog zum Anlegen oder bearbeiten einer Buchung
 *
 * @author Mike Wiebe
 */
public class BuchungAnlegenBearbeitenDialog extends Dialog {

    // Services
    private final RaumService roomService;
    private final DozentService dozentService;
    private final BuchungService buchungService;
    private final VeranstaltungService veranstaltungService;

    // UI
    private final Binder<Buchung> binder = new Binder<>(Buchung.class);
    private final ComboBox<Raum> raum = new ComboBox<>("Raumnummer");
    private final ComboBox<Veranstaltung> veranstaltung = new ComboBox<>("Veranstaltung");
    private final ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
    private final DatePicker date = new DatePicker("Datum");
    private final Button save = new Button("Speichern");
    private final Button cancel = new Button("Abbrechen");
    private final RadioButtonGroup<Wiederholungsintervall> wiederholungsintervallRadioButtonGroup = new RadioButtonGroup<>("Wiederholungsintervall");
    private final DatePicker endDatum = new DatePicker("Letzter Buchungstag");
    private final ComboBox<Zeitslot> zeitslot = new ComboBox<>("Zeitslot");

    // Variablen
    private final Buchung selectedBuchung;
    private final Raum selectedRoom;
    private final Veranstaltung selectedVeranstaltung;
    private final AuthenticatedUser currentUser;
    private final Buchung initialBuchung;

    public BuchungAnlegenBearbeitenDialog(Buchung selectedBuchung, Raum selectedRoom, Veranstaltung selectedVeranstaltung, RaumService roomService,
                                          DozentService dozentService, BuchungService buchungService, VeranstaltungService veranstaltungService, AuthenticatedUser currentUser) {
        this.roomService = roomService;
        this.dozentService = dozentService;
        this.buchungService = buchungService;
        this.veranstaltungService = veranstaltungService;
        this.selectedBuchung = selectedBuchung;
        this.selectedRoom = selectedRoom;
        this.selectedVeranstaltung = selectedVeranstaltung;
        this.currentUser = currentUser;

        initialBuchung = selectedBuchung != null ? new Buchung(selectedBuchung) : null;

        add(createInputLayout());
        createButtonLayout();
    }

    /**
     * Methode zum Erstellen der Eingabemaske um eine Buchung anzulegen
     *
     * @return FormLayout
     * @author Mike Wiebe
     */
    private FormLayout createInputLayout() {
        FormLayout dialogLayout = new FormLayout();

        raum.setItems(roomService.findAll());
        //Dozenten und die FBPlanung sollen nur Räume buchen können, die auch in ihren Fachbereich fallen
        if (currentUser.get().isPresent()) {
            if (currentUser.get().get().getRoles().contains(Role.DOZENT) || currentUser.get().get().getRoles().contains(Role.FBPLANUNG)) {
                raum.setItems(roomService.findAllByFachbereich(currentUser.get().get().getFachbereich()));
            }
        }
        raum.setItemLabelGenerator(Raum::getRefNr);
        raum.setRequiredIndicatorVisible(true);

        veranstaltung.setItems(veranstaltungService.findAll());
        veranstaltung.setItemLabelGenerator(Veranstaltung::getBezeichnung);
        veranstaltung.setRequiredIndicatorVisible(true);
        veranstaltung.setId("combobox-veranstaltung");

        dozent.setItems(dozentService.findAll());
        //Wenn der Nutzer ein Dozent ist, soll dieser beim Anlegen der Buchung selbst als Dozent eingetragen und nicht änderbar sein damit jeder Dozent nur Buchungen für sich anlegen kann
        if (currentUser.get().isPresent()) {
            if (currentUser.get().get().getRoles().contains(Role.DOZENT)) {
                Optional<Dozent> currentDozentOptional = dozentService.findByVornameAndNachname(currentUser.get().get().getFirstName(), currentUser.get().get().getLastName());
                if (currentDozentOptional.isPresent()) {
                    dozent.setItems(currentDozentOptional.get());
                    dozent.setValue(currentDozentOptional.get());
                }
                dozent.setEnabled(false);
            }
        }
        dozent.setRequiredIndicatorVisible(true);
        dozent.setId("combobox-dozent");

        date.setLabel("Datum");
        date.setRequiredIndicatorVisible(true);
        date.setId("datepicker-startdate");

        zeitslot.setItems(Zeitslot.values());
        zeitslot.setId("combobox-zeitslot");

        wiederholungsintervallRadioButtonGroup.setItems(Wiederholungsintervall.values());
        wiederholungsintervallRadioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        wiederholungsintervallRadioButtonGroup.setValue(Wiederholungsintervall.EINMALIG);
        endDatum.setVisible(false);
        wiederholungsintervallRadioButtonGroup.addValueChangeListener(e -> {
            if (e.getValue().equals(Wiederholungsintervall.EINMALIG)) {
                endDatum.setVisible(false);
            } else {
                endDatum.setVisible(true);
                binder.forField(endDatum).asRequired();
            }
        });
        wiederholungsintervallRadioButtonGroup.setId("radiogroup-wiederholungsintervall");
        endDatum.setId("datepicker-enddate");

        binder.forField(raum)
                .asRequired("Bitte wählen Sie einem Raum aus").bind(Buchung::getRoom, Buchung::setRoom);
        binder.forField(veranstaltung).asRequired("Bitte wählen Sie eine Veranstaltung aus").bind(Buchung::getVeranstaltung, Buchung::setVeranstaltung);
        binder.forField(dozent).asRequired("Bitte wählen Sie einen Dozent aus").bind(Buchung::getDozent, Buchung::setDozent);
        binder.forField(date).asRequired("Bitte wählen Sie ein Datum aus").bind(Buchung::getDate, Buchung::setDate);

        // Buchung bearbeiten
        if (selectedBuchung != null) {
            binder.forField(zeitslot).asRequired("Bitte wählen Sie einen Zeitslot aus")
                    .bind(Buchung::getZeitslot, Buchung::setZeitslot);
            binder.readBean(selectedBuchung);
        } else {
            binder.forField(zeitslot).asRequired("Bitte wählen Sie einen Zeitslot aus")
                    .withValidator(event -> !buchungService.roomBooked(raum.getValue(), zeitslot.getValue(), date.getValue()), "Zeitslot bereits belegt")
                    .bind(Buchung::getZeitslot, Buchung::setZeitslot);
            binder.forField(raum)
                    .withValidator(event -> !buchungService.roomBooked(raum.getValue(), zeitslot.getValue(), date.getValue()), "Raum bereits belegt");
        }
        // Einstieg in Dialog über Raum
        if (selectedRoom != null) {
            raum.setValue(selectedRoom);
            raum.setEnabled(false);
            zeitslot.setEnabled(true);
        }
        // Einstieg in Dialog über Veranstaltung
        if (selectedVeranstaltung != null) {
            veranstaltung.setValue(selectedVeranstaltung);
            veranstaltung.setEnabled(false);
        }

        dialogLayout.add(raum, date, veranstaltung, dozent, zeitslot);
        // Kein Wiederholungsintervall bei Buchung bearbeiten
        if (selectedBuchung == null) {
            dialogLayout.add(wiederholungsintervallRadioButtonGroup, endDatum);
        }

        dialogLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2));
        dialogLayout.setColspan(veranstaltung, 2);
        dialogLayout.setColspan(dozent, 2);
        dialogLayout.setColspan(zeitslot, 2);
        dialogLayout.setMaxWidth("25vw");

        return dialogLayout;

    }

    /**
     * Methode für die Speichern und Abbrechen Buttons
     *
     * @author Mike Wiebe
     */
    private void createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        save.addClickListener(event -> {
            if (validateAndSave()) {
                close();
            }
        });
        save.setId("button-speichern");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(event -> close());
        this.getFooter().add(save, cancel);
    }

    /**
     * Methode zum Prüfen und Speichern einer eingegebenen Buchung
     *
     * @return boolean, Speichern erfolgreich oder nicht
     * @author Mike Wiebe
     */
    private boolean validateAndSave() {
        Wiederholungsintervall wiederholungsintervall = wiederholungsintervallRadioButtonGroup.getValue();
        Buchung firstBuchung;
        firstBuchung = selectedBuchung != null ? selectedBuchung : new Buchung();

        // Wenn eine Buchung ausgewählt wurde, werden hier die Binder zur Belegung nicht geprüft, da die Validation nicht hinzufügt wurden.
        if (binder.writeBeanIfValid(firstBuchung)) {
            if (currentUser.get().isPresent()) {
                firstBuchung.setUser(currentUser.get().get());
            }
        } else {
            Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
            return false;
        }

        // Erste Buchung wird immer gespeichert, wenn alle Binder erfolgreich.
        // Wenn eine Buchung gewählt wurde, werden nur die Felder aktualisiert, die geändert wurden.
        // Wiederholungsintervall ist automatisch einmalig, wenn Buchung geändert wird.
        if (wiederholungsintervall == Wiederholungsintervall.EINMALIG) {
            if (selectedBuchung != null) {
                // Veranstaltung und Dozent können geändert werden
                if (initialBuchung.getZeitslot() != firstBuchung.getZeitslot() || !initialBuchung.getDate().equals(firstBuchung.getDate()) || initialBuchung.getRoom() != firstBuchung.getRoom()) {
                    if (buchungService.roomBooked(firstBuchung.getRoom(), firstBuchung.getZeitslot(), firstBuchung.getDate())) {
                        Notification.show("Der Raum ist bereits belegt", 4000, Notification.Position.MIDDLE);
                        return false;
                    }
                }
            }
            buchungService.save(firstBuchung);
            Notification sucessNotification = Notification.show("Buchung gespeichert", 4000, Notification.Position.MIDDLE);
            sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            return true;
        }
        // Wiederholungsintervall soll bei existierenden Buchungen nicht beachtet werden, sondern immer nur die einzelne Buchung wird geändert
        if (selectedBuchung == null) {
            if (wiederholungsintervall == Wiederholungsintervall.WOECHENTLICH || wiederholungsintervall == Wiederholungsintervall.TAEGLICH || wiederholungsintervall == Wiederholungsintervall.JAEHRLICH) {
                // Buchungen werden erst in einer Liste gespeichert und dann wird geschaut welche Konflikte auftreten
                List<Buchung> gespeicherteBuchungen = new ArrayList<>();
                gespeicherteBuchungen.add(firstBuchung);
                LocalDate currentDate = firstBuchung.getDate();
                LocalDate endDate = endDatum.getValue();
                while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
                    Buchung nextBuchung = new Buchung(firstBuchung);
                    nextBuchung.setDate(currentDate);
                    nextBuchung.setUser(currentUser.get().get());
                    gespeicherteBuchungen.add(nextBuchung);
                    if (wiederholungsintervall == Wiederholungsintervall.WOECHENTLICH) {
                        currentDate = currentDate.plusDays(7);
                    } else if (wiederholungsintervall == Wiederholungsintervall.TAEGLICH) {
                        currentDate = currentDate.plusDays(1);
                    } else {
                        currentDate = currentDate.plusYears(1); // wiederholungsintervall jährlich
                    }
                }
                // Gibt es einen Konflikt bei einer Buchung in den gespeicherten Buchungen?
                // Dialog mit allen Konflikten wird angezeigt, wenn es Konflikte gibt
                boolean fehlerBeiBuchung = false;
                String fehlertext = "Der " + raum.getValue() + " ist bereits an folgenden Terminen belegt: \n";
                for (Buchung buchung : gespeicherteBuchungen) {
                    Optional<Buchung> konfliktBuchung = buchungService.findByDateAndRoomAndZeitslot(buchung.getDate(), buchung.getRoom(), buchung.getZeitslot());
                    if (konfliktBuchung.isPresent()) {
                        fehlertext =
                                fehlertext.concat(buchung.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " von " + buchung.getZeitslot() + ": " + konfliktBuchung.get().getVeranstaltung().toString() +
                                        "\n");
                        fehlerBeiBuchung = true;
                    }
                }
                fehlertext = fehlertext.concat("Es wurden keine Buchungen erstellt.");
                if (fehlerBeiBuchung) {
                    Dialog errorDialog = new Dialog();
                    errorDialog.setResizable(true);
                    TextArea errorTextArea = new TextArea();
                    errorTextArea.setReadOnly(true);
                    errorTextArea.setWidth("40vw");
                    errorTextArea.setValue(fehlertext);
                    errorDialog.add(errorTextArea);
                    errorDialog.getFooter().add(new Button("Fenster schließen", event -> errorDialog.close()));
                    errorDialog.open();
                    return false;
                } else {
                    // Keine Fehler -> Alle Buchungen speichern
                    for (Buchung buchung : gespeicherteBuchungen) {
                        buchungService.save(buchung);
                    }
                    Notification sucessNotification = Notification.show("Buchungen erfolgreich gespeichert", 4000, Notification.Position.MIDDLE);
                    sucessNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    return true;
                }
            }
        }
        return false;
    }
}

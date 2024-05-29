package com.example.application.views;

import com.example.application.data.dataProvider.RegistrierungDataProvider;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Registrierung;
import com.example.application.data.entities.Role;
import com.example.application.services.DozentService;
import com.example.application.services.EmailService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.crud.CrudVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

@Route(value = "freischalten", layout = MainLayout.class)
@PageTitle("User freischalten")
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class FreischaltenView extends VerticalLayout {

    private final Crud<Registrierung> crud;
    private final RegistrierungDataProvider registrierungDataProvider;
    private final UserService userService;
    private final EmailService emailService;
    private final DozentService dozentService;

    public FreischaltenView(UserService userService, EmailService emailService, DozentService dozentService) {
        this.userService = userService;
        this.registrierungDataProvider = new RegistrierungDataProvider(userService);
        this.emailService = emailService;

        this.crud = new Crud<>(Registrierung.class, new BinderCrudEditor<>(new BeanValidationBinder<>(Registrierung.class), new FormLayout()));
        crud.addThemeVariants(CrudVariant.NO_BORDER);
        //ohne "New Item" button
        crud.setToolbarVisible(false);
        crud.setDataProvider(registrierungDataProvider);
        setupGrid();
        setupLanguage();
        add(crud);
        this.dozentService = dozentService;
    }

    private void setupGrid() {
        Grid<Registrierung> grid = crud.getGrid();

        grid.removeColumnByKey("id");
        grid.removeColumnByKey("hashedPassword");

        // ohne "Edit" button
        grid.removeColumnByKey("vaadin-crud-edit-column");

        grid.getColumnByKey("username").setHeader("Benutzername");
        grid.getColumnByKey("firstName").setHeader("Vorname");
        grid.getColumnByKey("lastName").setHeader("Nachname");
        grid.getColumnByKey("role").setHeader("Rolle");
        grid.getColumnByKey("fachbereich").setHeader("Fachbereich");


        grid.addComponentColumn(registrierung -> {
            Button approveButton = new Button("Freischalten");
            approveButton.addClickListener(event -> approveRegistration(registrierung));
            return approveButton;
        }).setHeader("Freischalten");

        // Make the new "Actions" column fixed at the end
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
    }

    private void approveRegistration(Registrierung registrierung) {
        try {
            userService.approveRegistration(registrierung);
            registrierungDataProvider.refreshAll();
            Notification.show("User wurde freigeschaltet", 3000, Notification.Position.MIDDLE);
            emailService.sendAprovedMail(registrierung.getUsername());
            registrierungDataProvider.update();
            if (registrierung.getRole() == Role.DOZENT) {
                Dozent newDozent = new Dozent();
                newDozent.setFachbereich(registrierung.getFachbereich());
                newDozent.setNachname(registrierung.getLastName());
                newDozent.setVorname(registrierung.getFirstName());
                dozentService.save(newDozent);
            }
        } catch (Exception e) {
            Notification.show("Error bei der Freischaltung: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void setupLanguage() {
        CrudI18n i18n = CrudI18n.createDefault();
        i18n.setNewItem("Neuer Eintrag");
        i18n.setEditItem("Bearbeiten");
        i18n.setSaveItem("Speichern");
        i18n.setCancel("Abbrechen");
        i18n.setDeleteItem("Löschen");
        i18n.setEditLabel("Bearbeiten");

        CrudI18n.Confirmations.Confirmation delete = i18n.getConfirm().getDelete();
        delete.setTitle("Eintrag löschen");
        delete.setContent("Sind Sie sicher, dass Sie diesen Eintrag löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.");
        delete.getButton().setConfirm("Bestätigen");
        delete.getButton().setDismiss("Zurück");

        CrudI18n.Confirmations.Confirmation cancel = i18n.getConfirm().getCancel();
        cancel.setTitle("Änderungen verwerfen");
        cancel.setContent("Sie haben Änderungen an diesem Eintrag vorgenommen, die noch nicht gespeichert wurden.");
        cancel.getButton().setConfirm("Verwerfen");
        cancel.getButton().setDismiss("Zurück");

        crud.setI18n(i18n);
    }
}



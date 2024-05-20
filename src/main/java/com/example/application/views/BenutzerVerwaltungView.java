package com.example.application.views;

import com.example.application.data.dataProvider.UserDataProvider;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Role;
import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

/**
 * @author tim R
 */

@Route(value = "user-crud", layout = MainLayout.class)
@PageTitle("BenutzerVerwaltung")
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)

public class BenutzerVerwaltungView extends VerticalLayout {

    private final UserService userService;

    private final String FACHBEREICH = "fachbereich";
    private final String VORNAME = "firstNAme";
    private final String NACHNAME = "lastName";
    private final String EMAIL = "username";
    private final String FREIGESCHALTEN = "locked";
    private final String ROLLE = "roles";
    private final String EDIT_COLUMN = "vaadin-crud-edit-column";

    private final Crud<User> crud;

    /**
     *
     * @param userService für Benutzer

     */

    public BenutzerVerwaltungView(UserService userService) {
        this.userService = userService;

        crud = new Crud<>(User.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupLanguage();

        add(crud);
    }



    private CrudEditor<User> createEditor() {
        TextField vorname = new TextField("Vorname");
        TextField nachname = new TextField("Nachname");
        EmailField email = new EmailField("Email");
        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);
        ComboBox<Role> rolle = new ComboBox<>("Rolle");
        rolle.setItems(Role.values());
        rolle.setItemLabelGenerator(Role::toString);
        Checkbox freigeschaltet = new Checkbox("Freigeschaltet");

        FormLayout form = new FormLayout(vorname, nachname, email, fachbereich, rolle, freigeschaltet);

        Binder<User> binder = new Binder<>(User.class);
        binder.forField(vorname).asRequired().bind(User::getFirstName, User::setFirstName);
        binder.forField(nachname).asRequired().bind(User::getLastName, User::setLastName);
        binder.forField(email).asRequired().bind(User::getUsername, User::setUsername);
        binder.forField(fachbereich).asRequired().bind(User::getFachbereich, User::setFachbereich);
        //binder.forField(rolle).asRequired().bind(User::getRoles, User::setRoles); //Roles?
        binder.forField(freigeschaltet).bind(User::isLocked, User::setLocked);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<User> grid = crud.getGrid();

        grid.removeColumn(grid.getColumnByKey("id"));
        grid.removeColumn(grid.getColumnByKey("hashedPassword"));



        grid.getColumnByKey(EDIT_COLUMN).setFrozenToEnd(true);

//        grid.setColumnOrder(grid.getColumnByKey(VORNAME),
//                grid.getColumnByKey(NACHNAME),
//                grid.getColumnByKey(EMAIL),
//                grid.getColumnByKey(FACHBEREICH),
//                grid.getColumnByKey(ROLLE),
//                grid.getColumnByKey(FREIGESCHALTEN),
//                grid.getColumnByKey(EDIT_COLUMN));
    }

    private void setupDataProvider() {
        UserDataProvider dataProvider = new UserDataProvider(userService);
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(deleteEvent -> {
            dataProvider.delete(deleteEvent.getItem());
            dataProvider.refreshAll();
        });
        crud.addSaveListener(saveEvent -> {
            dataProvider.save(saveEvent.getItem());
            dataProvider.refreshAll();
        });
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

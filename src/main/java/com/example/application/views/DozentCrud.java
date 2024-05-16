package com.example.application.views;

import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Role;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "dozent-crud", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@PageTitle("Dozenten")
public class DozentCrud extends Div {

    private final Crud<Dozent> crud;

    public DozentCrud() {
        crud = new Crud<>(Dozent.class, createEditor());

        setupGrid();
        setupLanguage();

        add(crud);
    }

    private CrudEditor<Dozent> createEditor() {
        TextField username = new TextField("Benutzername");
        TextField nachname = new TextField("Nachname");
        TextField vorname = new TextField("Vorname");
        MultiSelectComboBox<Fachbereich> fachbereich = new MultiSelectComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        MultiSelectComboBox<Role> roles = new MultiSelectComboBox<>("Rollen");
        roles.setItems(Role.values());

        FormLayout form = new FormLayout(username, vorname, nachname, fachbereich, roles);

        Binder<Dozent> binder = new Binder<>(Dozent.class);
        binder.forField(username).asRequired("Benutzername ist erforderlich").bind(Dozent::getUsername, Dozent::setUsername);
        binder.forField(nachname).asRequired("Nachname ist erforderlich").bind(Dozent::getNachname, Dozent::setNachname);
        binder.forField(vorname).asRequired("Vorname ist erforderlich").bind(Dozent::getVorname, Dozent::setVorname);
        binder.forField(fachbereich).bind(Dozent::getFachbereich, Dozent::setFachbereich);
        binder.forField(roles).bind(Dozent::getRoles, Dozent::setRoles);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<Dozent> grid = crud.getGrid();
        grid.addColumn(Dozent::getUsername).setHeader("Benutzername");
        grid.addColumn(Dozent::getVorname).setHeader("Vorname");
        grid.addColumn(Dozent::getNachname).setHeader("Nachname");
        grid.addColumn(dozent -> dozent.getFachbereich().toString()).setHeader("Fachbereiche");
        grid.addColumn(dozent -> dozent.getRoles().toString()).setHeader("Rollen");
    }

    private void setupLanguage() {
        CrudI18n i18n = CrudI18n.createDefault();
        i18n.setNewItem("Neuer Dozent");
        i18n.setEditItem("Dozent bearbeiten");
        i18n.setSaveItem("Speichern");
        i18n.setCancel("Abbrechen");
        i18n.setDeleteItem("Löschen");
        i18n.setEditLabel("Dozent bearbeiten");

        crud.setI18n(i18n);
    }
}

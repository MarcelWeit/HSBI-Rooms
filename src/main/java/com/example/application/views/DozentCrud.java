package com.example.application.views;

import com.example.application.data.dataProvider.DozentDataProvider;
import com.example.application.data.entities.Dozent;
import com.example.application.data.entities.Fachbereich;
import com.example.application.services.DozentService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

/**
 * @author Gabriel
 */
@Route(value = "dozent-crud", layout = MainLayout.class)
@Secured({"ADMIN", "FBPLANUNG"})
@RolesAllowed({"ADMIN", "FBPLANUNG"})
@Uses(Icon.class)
@PageTitle("Dozenten")
public class DozentCrud extends Div {

    private final DozentService dozentService;

    private final Crud<Dozent> crud;

    private final String ID = "id";
    private final String NACHNAME = "nachname";
    private final String VORNAME = "vorname";
    private final String FACHBEREICH = "fachbereich";
    private final String EDIT_COLUMN = "vaadin-crud-edit-column";

    public DozentCrud(DozentService dozentService) {
        this.dozentService = dozentService;

        crud = new Crud<>(Dozent.class, createEditor());

        setupGrid();
        setupDataProvider();
        setupLanguage();

        add(crud);
    }

    private CrudEditor<Dozent> createEditor() {

        TextField nachname = new TextField("Nachname");
        TextField vorname = new TextField("Vorname");

        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        fachbereich.setItemLabelGenerator(Fachbereich::toString);
        fachbereich.setRequired(true);

        FormLayout form = new FormLayout(nachname, vorname, fachbereich);

        Binder<Dozent> binder = new Binder<>(Dozent.class);
        binder.forField(nachname).asRequired().bind(Dozent::getNachname, Dozent::setNachname);
        binder.forField(vorname).asRequired().bind(Dozent::getVorname, Dozent::setVorname);
        binder.forField(fachbereich).asRequired().bind(Dozent::getFachbereich, Dozent::setFachbereich);

        return new BinderCrudEditor<>(binder, form);
    }

    private void setupGrid() {
        Grid<Dozent> grid = crud.getGrid();
        grid.getColumnByKey(EDIT_COLUMN).setFrozenToEnd(true);


        grid.setColumnOrder(
                grid.getColumnByKey(ID),
                grid.getColumnByKey(FACHBEREICH),
                grid.getColumnByKey(NACHNAME),
                grid.getColumnByKey(VORNAME),
                grid.getColumnByKey(EDIT_COLUMN));

        grid.removeColumnByKey(ID);
    }

    private void setupDataProvider() {
        DozentDataProvider dataProvider = new DozentDataProvider(dozentService);
        crud.setDataProvider(dataProvider);
        crud.addDeleteListener(deleteEvent -> {
            dataProvider.delete(deleteEvent.getItem());
            dataProvider.refreshAll();
        });
        crud.addSaveListener(saveEvent -> {
            try {
                dataProvider.save(saveEvent.getItem());
                dataProvider.refreshAll(); // Refresh the grid after saving
            } catch (IllegalArgumentException e) {
                Notification.show(e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
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

        CrudI18n.Confirmations.Confirmation delete = i18n.getConfirm()
                .getDelete();
        delete.setTitle("Eintrag löschen");
        delete.setContent(
                "Sind Sie sicher, dass Sie diesen Eintrag löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.");
        delete.getButton().setConfirm("Bestätigen");
        delete.getButton().setDismiss("Zurück");

        CrudI18n.Confirmations.Confirmation cancel = i18n.getConfirm()
                .getCancel();
        cancel.setTitle("Änderungen verwerfen");
        cancel.setContent("Sie haben Änderungen an diesem Eintrag vorgenommen, die noch nicht gespeichert wurden.");
        cancel.getButton().setConfirm("Verwerfen");
        cancel.getButton().setDismiss("Zurück");

        crud.setI18n(i18n);
    }

}

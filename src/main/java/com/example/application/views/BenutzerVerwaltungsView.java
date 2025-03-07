package com.example.application.views;

import com.example.application.comparator.LastNameComparator;
import com.example.application.data.entities.User;
import com.example.application.data.enums.Fachbereich;
import com.example.application.data.enums.Role;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * View zur Verwaltung der Benutzer in der User Klasse
 */
@Route(value = "Benutzerverwaltung", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
@PageTitle("Benutzer verwalten")
public class BenutzerVerwaltungsView extends VerticalLayout {

    private final AuthenticatedUser currentUser;
    private final UserService userService;
    private final Grid<User> userGrid = new Grid<>(User.class, false);
    private final Binder<User> userBinder = new Binder<>(User.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    public BenutzerVerwaltungsView(UserService userService, AuthenticatedUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
        //Grid und Buttons Einrichten
        setupButtons();
        setupGrid();
        add(buttonLayout, userGrid);
    }

    // Methode zur Erstellung des Filter-Headers
    private static Component createStringFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }

    // Methode zur Einrichtung des Grids
    private void setupGrid() {
        GridListDataView<User> dataView = userGrid.setItems(userService.findAll());

        userGrid.addColumn(User::getLastName).setHeader("Nachname")
                .setComparator(new LastNameComparator())
                .setKey("nachname");
        userGrid.addColumn(User::getFirstName).setHeader("Vorname").setKey("vorname");
        userGrid.addColumn(User::getFachbereich).setHeader("Fachbereich").setKey("fachbereich");
        userGrid.addColumn(User::getRoles).setHeader("Rolle").setKey("role");
        userGrid.addColumn(User::getUsername).setHeader("Benutzername").setKey("benutzername");


        userGrid.getColumnByKey("nachname").setHeader("Nachname");
        userGrid.getColumnByKey("vorname");
        userGrid.getColumnByKey("fachbereich");
        userGrid.getColumnByKey("role");
        userGrid.getColumnByKey("benutzername");


        // Sortierung nach Nachname
        GridSortOrder<User> sortOrder = new GridSortOrder<>(userGrid.getColumnByKey("nachname"), SortDirection.ASCENDING);
        ArrayList<GridSortOrder<User>> sortOrders = new ArrayList<>();
        sortOrders.add(sortOrder);
        userGrid.sort(sortOrders);

        userGrid.setMinHeight("80vh");

        setupFilter(dataView);
    }

    // Methode zur Einrichtung der Buttons
    private void setupButtons() {

        // Button zum Bearbeiten eines Benutzers
        Button editUserButton = new Button("Benutzer bearbeiten", new Icon(VaadinIcon.EDIT));
        editUserButton.addClickListener(e -> {
            Optional<User> selectedUser = userGrid.getSelectionModel().getFirstSelectedItem();
            if (selectedUser.isEmpty()) {
                Notification.show("Bitte wählen Sie einen Benutzer aus", 2000, Notification.Position.MIDDLE);
            } else {
                openEditCreateDialog(selectedUser);
            }
        });
        // Button zum Löschen eines Benutzers
        Button deleteUserButton = new Button("Benutzer Löschen", new Icon(VaadinIcon.TRASH));
        deleteUserButton.addClickListener(e -> openDeleteDialog());

        buttonLayout.add(editUserButton, deleteUserButton);
    }

    // Methode zum Öffnen des Dialogs zum Bearbeiten oder Erstellen eines Benutzers
    private void openEditCreateDialog(Optional<User> selectedUser) {
        Dialog dialog = new Dialog();
        dialog.setMaxWidth("25vw");
        dialog.setMinWidth("200px");
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        TextField nachname = new TextField("Nachname");
        TextField vorname = new TextField("Vorname");
        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
        TextField username = new TextField("Username");
        MultiSelectListBox<String> role = new MultiSelectListBox<>();
        role.getElement().getStyle().set("border-radius", "20px");
        role.getElement().getStyle().set("margin", "1em");
        role.getElement().getStyle().set("padding", "0.5em");
        role.setItems("ADMIN", "DOZENT", "FBPLANUNG");

        form.add(nachname, vorname, fachbereich, username, role);
        dialog.add(form);

        userBinder.forField(nachname).asRequired("Nachname required").bind(User::getLastName, User::setLastName);
        userBinder.forField(vorname).asRequired("Vorname required").bind(User::getFirstName, User::setFirstName);
        userBinder.forField(fachbereich).asRequired("Fachbereich required").bind(User::getFachbereich, User::setFachbereich);
        userBinder.forField(username).asRequired("Username required").bind(User::getUsername, User::setUsername);
        userBinder.forField(role)
                .bind(
                        user -> user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                        (user, rolesString) -> user.setRoles(rolesString.stream()
                                .map(Role::valueOf)
                                .collect(Collectors.toSet()))
                );

        if (selectedUser.isPresent()) {
            userBinder.readBean(selectedUser.get());
            username.setEnabled(false); // Username should not be editable
        }
        // Buttons zum Speichern und Abbrechen
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            if (role.isEmpty()) {
                Notification.show("Role required", 2000, Notification.Position.MIDDLE);
            } else {
                User user = selectedUser.orElseGet(User::new);
                if (userBinder.writeBeanIfValid(user) || selectedUser.isPresent()) {
                    userService.save(user);
                    userGrid.setItems(userService.findAll());
                    dialog.close();
                }
            }
        });

        dialog.getFooter().add(cancelButton, saveButton);

        dialog.open();
    }

    // Methode zum Öffnen des Dialogs zum Löschen eines Benutzers
    private void openDeleteDialog() {
        Optional<User> selectedUser = userGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedUser.isEmpty()) {
            Notification.show("Bitte einen User Auswählen", 2000, Notification.Position.MIDDLE);
        } else {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("User " + selectedUser.get().getUsername() + " löschen?");
            confirmDeleteDialog.setText("Diese Aktion kann nicht rückgängig gemacht werden.");

            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmButtonTheme("error primary");

            confirmDeleteDialog.setConfirmButton("Löschen", event -> {
                userService.delete(selectedUser.get());
                userGrid.setItems(userService.findAll());
                confirmDeleteDialog.close();
            });

            confirmDeleteDialog.setCancelButton("Abbrechen", event -> confirmDeleteDialog.close());

            confirmDeleteDialog.open();
        }
    }

    // Methode zum Einrichten des Filters
    private void setupFilter(GridListDataView<User> dataView) {
        UserFilter userFilter = new UserFilter(dataView);

        userGrid.getHeaderRows().clear();
        HeaderRow headerRow = userGrid.appendHeaderRow();

        headerRow.getCell(userGrid.getColumnByKey("nachname")).setComponent(createStringFilterHeader(userFilter::setLastName));
        headerRow.getCell(userGrid.getColumnByKey("vorname")).setComponent(createStringFilterHeader(userFilter::setFirstName));
        headerRow.getCell(userGrid.getColumnByKey("fachbereich")).setComponent(createStringFilterHeader(userFilter::setFachbereich));
        headerRow.getCell(userGrid.getColumnByKey("role")).setComponent(createStringFilterHeader(userFilter::setRole));
        headerRow.getCell(userGrid.getColumnByKey("benutzername")).setComponent(createStringFilterHeader(userFilter::setUsername));
    }

    // Klasse zum Filtern der Benutzer
    private static class UserFilter {
        private final GridListDataView<User> dataView;

        private String lastName;
        private String firstName;
        private String fachbereich;
        private String role;
        private String username;

        // Konstruktor
        public UserFilter(GridListDataView<User> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        // Setter
        public void setLastName(String lastName) {
            this.lastName = lastName;
            this.dataView.refreshAll();
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
            this.dataView.refreshAll();
        }

        public void setFachbereich(String fachbereich) {
            this.fachbereich = fachbereich;
            this.dataView.refreshAll();
        }

        public void setRole(String role) {
            this.role = role;
            this.dataView.refreshAll();
        }

        public void setUsername(String username) {
            this.username = username;
            this.dataView.refreshAll();
        }

        // Methode zum Testen, ob ein Benutzer den Filterkriterien entspricht
        public boolean test(User user) {
            return matches(user.getLastName(), lastName)
                    && matches(user.getFirstName(), firstName)
                    && matches(user.getFachbereich().toString(), fachbereich)
                    && matches(user.getRoles().stream().map(Enum::name).collect(Collectors.joining(",")), role)
                    && matches(user.getUsername(), username);
        }

        // Methode zum Überprüfen, ob ein Wert einem Suchbegriff entspricht
        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}


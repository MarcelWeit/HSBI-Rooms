package com.example.application.views;

import com.example.application.data.entities.User;
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
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;
import java.util.function.Consumer;

public class BenutzerVerwaltungsView extends VerticalLayout {

    private final AuthenticatedUser currentUser;
    private final UserService userService;
    private final Grid<User> userGrid = new Grid<>(User.class, false);
    private final Binder<User> userBinder = new Binder<>(User.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();

    public BenutzerVerwaltungsView(UserService userService, AuthenticatedUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;

        setupButtons();
        setupGrid();
        add(buttonLayout, userGrid);
    }

    private static Component createStringFilterHeader(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }

    private void setupGrid() {
        GridListDataView<User> dataView = userGrid.setItems(userService.findAll());

        userGrid.addColumn(User::getId).setHeader("ID").setKey("id");
        userGrid.addColumn(User::getUsername).setHeader("Username").setKey("username");

        userGrid.addColumn(User::getRoles).setHeader("Role").setKey("role");

        userGrid.getColumnByKey("id").setAutoWidth(true).setFlexGrow(0);
        userGrid.getColumnByKey("username").setAutoWidth(true).setFlexGrow(0);
        userGrid.getColumnByKey("email").setAutoWidth(true).setFlexGrow(0);
        userGrid.getColumnByKey("role").setAutoWidth(true).setFlexGrow(0);

        userGrid.setMinHeight("80vh");

        setupFilter(dataView);
    }

    private void setupButtons() {
        Button addUserButton = new Button("Add User", new Icon(VaadinIcon.PLUS));
        addUserButton.addClickListener(e -> openEditCreateDialog(Optional.empty()));

        Button editUserButton = new Button("Edit User", new Icon(VaadinIcon.EDIT));
        editUserButton.addClickListener(e -> {
            Optional<User> selectedUser = userGrid.getSelectionModel().getFirstSelectedItem();
            if (selectedUser.isEmpty()) {
                Notification.show("Please select a user", 2000, Notification.Position.MIDDLE);
            } else {
                openEditCreateDialog(selectedUser);
            }
        });

        Button deleteUserButton = new Button("Delete User", new Icon(VaadinIcon.TRASH));
        deleteUserButton.addClickListener(e -> openDeleteDialog());

        buttonLayout.add(addUserButton, editUserButton, deleteUserButton);
    }

    private void setupFilter(GridListDataView<User> dataView) {
        UserFilter userFilter = new UserFilter(dataView);

        userGrid.getHeaderRows().clear();
        HeaderRow headerRow = userGrid.appendHeaderRow();

        headerRow.getCell(userGrid.getColumnByKey("username")).setComponent(createStringFilterHeader(userFilter::setUsername));
        headerRow.getCell(userGrid.getColumnByKey("email")).setComponent(createStringFilterHeader(userFilter::setEmail));

        Consumer<String> roleFilterChangeConsumer = userFilter::setRole;
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setWidthFull();
        roleComboBox.setItems("ADMIN", "USER");
        roleComboBox.setClearButtonVisible(true);
        roleComboBox.addValueChangeListener(e -> roleFilterChangeConsumer.accept(e.getValue()));
        headerRow.getCell(userGrid.getColumnByKey("role")).setComponent(roleComboBox);
    }

    private void openEditCreateDialog(Optional<User> selectedUser) {
        Dialog dialog = new Dialog();
        dialog.setMaxWidth("25vw");
        dialog.setMinWidth("200px");
        FormLayout form = new FormLayout();

        TextField username = new TextField("Username");
        TextField email = new TextField("Email");
        ComboBox<String> role = new ComboBox<>("Role");
        role.setItems("ADMIN", "USER");

        form.add(username, email, role);
        dialog.add(form);

        userBinder.forField(username).asRequired("Username required").bind(User::getUsername, User::setUsername);

        userBinder.forField(role).asRequired("Role required").bind(User::getRoles, User::setRoles);

        if (selectedUser.isPresent()) {
            userBinder.readBean(selectedUser.get());
            username.setEnabled(false); // Username should not be editable
        }

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            User user = selectedUser.orElseGet(User::new);
            if (userBinder.writeBeanIfValid(user) || selectedUser.isPresent()) {
                userService.save(user);
                userGrid.setItems(userService.findAll());
                dialog.close();
            }
        });

        dialog.getFooter().add(cancelButton, saveButton);

        dialog.open();
    }

    private void openDeleteDialog() {
        Optional<User> selectedUser = userGrid.getSelectionModel().getFirstSelectedItem();
        if (selectedUser.isEmpty()) {
            Notification.show("Please select a user", 2000, Notification.Position.MIDDLE);
        } else {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("Delete user " + selectedUser.get().getUsername() + "?");
            confirmDeleteDialog.setText("This action cannot be undone.");

            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmButtonTheme("error primary");

            confirmDeleteDialog.setConfirmButton("Delete", event -> {
                userService.delete(selectedUser.get());
                userGrid.setItems(userService.findAll());
                confirmDeleteDialog.close();
            });

            confirmDeleteDialog.setCancelButton("Cancel", event -> confirmDeleteDialog.close());

            confirmDeleteDialog.open();
        }
    }

    private static class UserFilter {
        private final GridListDataView<User> dataView;

        private String username;
        private String email;
        private String role;

        public UserFilter(GridListDataView<User> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setUsername(String username) {
            this.username = username;
            this.dataView.refreshAll();
        }

        public void setEmail(String email) {
            this.email = email;
            this.dataView.refreshAll();
        }

        public void setRole(String role) {
            this.role = role;
            this.dataView.refreshAll();
        }

        public boolean test(User user) {
            boolean matchesUsername = matches(user.getUsername(), username);

            boolean matchesRole = matches(user.getRoles(), role);

            return matchesUsername && matchesRole;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }
    }
}


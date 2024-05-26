package com.example.application.views;

import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Role;
import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author marcel weithoener
 */
@PageTitle("Registrierung")
@AnonymousAllowed
@Route(value = "register")
public class RegistrationView extends VerticalLayout {

    private final Binder<User> binder = new Binder<>(User.class);
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final PasswordField confirmPassword = new PasswordField("Passwort bestätigen");
    private final TextField lastName = new TextField("Nachname");
    private final TextField firstName = new TextField("Vorname");
    private final EmailField email = new EmailField("E-Mail");
    private final PasswordField password = new PasswordField("Passwort");
    private final Button submitButton = new Button("Registrieren");
    private final ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
    private final Button backButton = new Button("Zurück");
    private boolean enablePasswordValidation = false;

    /**
     * Konstruktor für die Registrierungsseite
     *
     * @param userService
     * @param passwordEncoder
     */
    public RegistrationView(UserService userService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        addClassName("registration-view");
        createComponents();
        fillTestData();
    }

    /**
     * Komponenten für die Registrierung erzeugen
     */
    private void createComponents() {
        FormLayout form = new FormLayout();
        H2 title = new H2("HSBI Rooms");
        H4 subTitle = new H4("Registrierung");

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClassName("submit-button");
        fachbereich.setItems(Fachbereich.values());
        //        ComboBox<Role> role = new ComboBox<>("Rolle");
        //        role.setItems(Role.values());
        backButton.addClickListener(e -> UI.getCurrent().navigate("login"));

        setupEventHandler();
        setupBinder();

        form.setMaxWidth("320px");
        form.add(title, subTitle, firstName, lastName, email, password, confirmPassword, fachbereich, submitButton, backButton);
        add(form);
    }

    /**
     * Event Handler für die Buttons erzeugen
     */
    private void setupEventHandler() {
        submitButton.addClickListener(e -> {
            User user = new User();
            if (binder.writeBeanIfValid(user)) {
                Set<Role> roles = new HashSet<>();
                roles.add(Role.ADMIN);
                user.setRoles(roles);
                user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
                userService.save(user);
                //User locked setzen, damit der Admin den User freischalten muss
                user.setLocked(true);
                UI.getCurrent().navigate("login");
            } else {
                Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
            }
        });
        submitButton.addClickShortcut(Key.ENTER);

        confirmPassword.addValueChangeListener(e -> {
            enablePasswordValidation = true;
            binder.validate();
        });
    }

    /**
     * Binder für die Formularfelder erzeugen
     */
    private void setupBinder() {
        binder.forField(firstName).asRequired().bind(User::getFirstName, User::setFirstName);
        binder.forField(lastName).asRequired().bind(User::getLastName, User::setLastName);
        binder.forField(email)
                .withValidator(new EmailValidator("invalid email", false))
                .withValidator(email -> !userService.emailExists(email), "Email already exists")
                .withValidationStatusHandler(status -> {
                    if (status.isError()) {
                        email.setErrorMessage("Ungültige E-Mail");
                    }
                })
                .bind(User::getUsername, User::setUsername);
        binder.forField(password)
                .asRequired()
                .withValidator(this::passwordValidator)
                .bind(User::getHashedPassword, User::setHashedPassword);
        binder.forField(fachbereich).asRequired().bind(User::getFachbereich, User::setFachbereich);
        binder.forField(confirmPassword).asRequired();
    }

    /**
     * @param password
     * @param valueContext
     * @return ValidationResult
     */
    private ValidationResult passwordValidator(String password, ValueContext valueContext) {
        if (password == null || password.length() < 8) {
            return ValidationResult.error("Password should be at least 8 characters long");
        }

        // erst validieren, wenn ein zweites passwort eingegeben wurde
        if (!enablePasswordValidation) {
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String confirmPasswordString = confirmPassword.getValue();

        if (confirmPasswordString != null && confirmPasswordString.equals(password)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }

    // TEMPORARY
    private void fillTestData() {
        firstName.setValue("Max");
        lastName.setValue("Mustermann");
        email.setValue("max@gmail.com");
        password.setValue("12345678");
        confirmPassword.setValue("12345678");
        fachbereich.setValue(Fachbereich.WIRTSCHAFT);
    }

}

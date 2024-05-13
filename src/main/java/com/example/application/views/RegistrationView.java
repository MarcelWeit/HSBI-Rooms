package com.example.application.views;

import com.example.application.data.entities.Fachbereich;
import com.example.application.data.entities.Role;
import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@PageTitle("Register")
@AnonymousAllowed
@Route(value = "register")
public class RegistrationView extends VerticalLayout {

    private final Binder<User> binder = new Binder<>(User.class);
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final PasswordField confirmPassword = new PasswordField("Passwort bestätigen");

    private boolean enablePasswordValidation = false;

    @Autowired
    public RegistrationView(UserService userService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        addClassName("registration-view");
        createComponents();
    }

    private void createComponents() {
        FormLayout form = new FormLayout();
        H1 title = new H1("Registrierung");
        TextField firstName = new TextField("Vorname");
        TextField lastName = new TextField("Nachname");
        EmailField email = new EmailField("E-Mail");
        PasswordField password = new PasswordField("Passwort");
        Button submitButton = new Button("Registrieren");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ComboBox<Fachbereich> fachbereich = new ComboBox<>("Fachbereich");
        fachbereich.setItems(Fachbereich.values());
//        ComboBox<Role> role = new ComboBox<>("Rolle");
//        role.setItems(Role.values());
        Button backButton = new Button("Zurück");
        backButton.addClickListener(e -> UI.getCurrent().navigate("login"));

        submitButton.addClickListener(e -> {
            User user = new User();
            if (binder.writeBeanIfValid(user)) {
                Set<Role> roles = new HashSet<>();
                roles.add(Role.ADMIN);
                user.setRoles(roles);
                user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
                userService.save(user);
                UI.getCurrent().navigate("login");
            } else {
                Notification.show("Bitte alle Felder korrekt befüllen", 4000, Notification.Position.MIDDLE);
            }
        });

        confirmPassword.addValueChangeListener(e -> {
            enablePasswordValidation = true;
            binder.validate();
        });

        binder.forField(firstName).asRequired().bind(User::getFirstName, User::setFirstName);
        binder.forField(lastName).asRequired().bind(User::getLastName, User::setLastName);
        binder.forField(email)
                .withValidator(new EmailValidator("invalid email", false))
                .withValidationStatusHandler(status -> {
                    if (status.isError()) {
                        email.setErrorMessage("Invalid email");
                    }
                })
                .bind(User::getUsername, User::setUsername);
        binder.forField(password)
                .asRequired()
                .withValidator(this::passwordValidator)
                .bind(User::getHashedPassword, User::setHashedPassword);
        binder.forField(fachbereich).asRequired().bind(User::getFachbereich, User::setFachbereich);
        binder.forField(confirmPassword).asRequired();

        form.setMaxWidth("500px");
        form.setColspan(title, 2);
        form.setColspan(email, 2);
        form.setColspan(submitButton, 2);

        form.add(title, firstName, lastName, email, password, confirmPassword, fachbereich, submitButton, backButton);
        setHorizontalComponentAlignment(Alignment.CENTER, form);
        add(form);
    }

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

        if(confirmPasswordString != null && confirmPasswordString.equals(password)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Passwords do not match");
    }

}

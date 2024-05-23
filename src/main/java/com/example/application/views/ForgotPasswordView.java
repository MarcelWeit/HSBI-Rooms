package com.example.application.views;

import com.example.application.services.UserService;
import com.example.application.services.EmailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Passwort vergessen")
@AnonymousAllowed
@Route(value = "forgot-password")
public class ForgotPasswordView extends Div {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public ForgotPasswordView(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
        addClassName("forgot-password-view");

        EmailField emailField = new EmailField("Bitte geben Sie Ihre E-Mail-Adresse an. Sie erhalten dann eine E-Mail mit einem Link, über den Sie ein neues Passwort wählen können.");
        emailField.setPlaceholder("E-Mail");
        emailField.setWidthFull();

        Button resetPasswordButton = new Button("Senden", event -> {
            if (emailField.isEmpty()) {
                Notification.show("Das E-Mail-Feld darf nicht leer sein.", 3000, Notification.Position.MIDDLE);
            } else {
                String email = emailField.getValue();
                if (userService.emailExists(email)) {
                    emailService.sendSimpleMessage(email, "Passwort zurücksetzen", "Hier ist der Link zum Zurücksetzen Ihres Passworts: <Link>");
                    Notification.show("Eine E-Mail zum Zurücksetzen des Passworts wurde gesendet.", 3000, Notification.Position.MIDDLE);
                } else {
                    Notification.show("Es gibt keinen Account, der mit dieser E-Mail-Adresse verknüpft ist.", 3000, Notification.Position.MIDDLE);
                }
            }
        });

        add(emailField, resetPasswordButton);
    }
}

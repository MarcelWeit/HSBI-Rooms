package com.example.application.views;

import com.example.application.services.UserService;
import com.example.application.services.EmailService;
import com.example.application.services.PasswordResetService;
import com.example.application.data.entities.User;
import com.example.application.data.entities.PasswordResetToken;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * View um ein neues Passwort zu beantragen
 *
 * @author Gabriel Greb
 */

@PageTitle("Passwort vergessen")
@AnonymousAllowed
@Route(value = "forgot-password")
public class ForgotPasswordView extends VerticalLayout  {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public ForgotPasswordView(UserService userService, EmailService emailService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;

        // Grundlegende Layout-Einstellungen
        addClassName("forgot-password-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Erstellen des Titels der Seite
        H2 title = new H2("Passwort vergessen?");
        title.getStyle().set("textAlign", "center");

        // Beschreibungstext zur Anleitung für Benutzer
        Div description = new Div();
        description.setText("Bitte geben Sie Ihre E-Mail an. Sie erhalten dann eine E-Mail mit einem Link, über den Sie ein neues Passwort wählen können.");
        description.getStyle().set("textAlign", "center");
        description.getStyle().set("marginBottom", "20px");

        // Eingabefeld für die E-Mail
        EmailField emailField = new EmailField();
        emailField.setPlaceholder("E-Mail");
        emailField.setWidthFull();

        // Button zum Senden der Anfrage zum Zurücksetzen des Passworts
        Button resetPasswordButton = new Button("Senden", event -> {
            // Überprüfung, ob das E-Mail-Feld leer ist
            if (emailField.isEmpty()) {
                Notification.show("Das E-Mail-Feld darf nicht leer sein.", 3000, Notification.Position.MIDDLE);
            } else {
                String email = emailField.getValue();   // E-Mail-Wert aus dem E-Mail-Feld holen
                // Überprüfen, ob ein Konto mit der eingegebenen E-Mail-Adresse existiert
                if (userService.emailExists(email)) {
                    User user = userService.findByUsername(email);// Benutzer anhand der E-Mail-Adresse finden

                    // Erstellen eines neuen Passwort-Reset-Tokens für den Benutzer
                    PasswordResetToken token = passwordResetService.createToken(user);

                    // URL für das Zurücksetzen des Passworts generieren, einschließlich des Tokens
                    String resetUrl = createPasswordResetUrl(token.getToken());

                    // Senden einer E-Mail an den Benutzer mit dem Link zum Zurücksetzen des Passworts
                    emailService.sendSimpleMessage(email, "Passwort zurücksetzen", "Hier ist der Link zum Zurücksetzen Ihres Passworts: " + resetUrl);

                    Notification.show("Eine E-Mail zum Zurücksetzen des Passworts wurde versendet.", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate("login"));
                } else {
                    Notification.show("Es gibt keinen Account, der mit dieser E-Mail-Adresse verknüpft ist.", 3000, Notification.Position.MIDDLE);
                }
            }
        });

        // Zurück zum Login-Button
        Button backButton = new Button("Zurück zum Login", event -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        // Layout konfiguration
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setWidth("100%");
        formLayout.getStyle().set("maxWidth", "400px");
        formLayout.getStyle().set("margin", "auto");
        formLayout.getStyle().set("padding", "20px");
        formLayout.getStyle().set("boxShadow", "0 2px 4px rgba(0, 0, 0, 0.1)");
        formLayout.getStyle().set("borderRadius", "8px");
        formLayout.add(title, description, emailField, resetPasswordButton, backButton);

        add(formLayout);
    }

    //Generierung eines Links zum Passwort zurücksetzen
    private String createPasswordResetUrl(String token) {
        return "http://localhost:8080/reset-password?token=" + token;
    }
}
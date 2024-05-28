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

@PageTitle("Passwort vergessen")
@AnonymousAllowed
@Route(value = "forgot-password")
public class ForgotPasswordView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public ForgotPasswordView(UserService userService, EmailService emailService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        addClassName("forgot-password-view");

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Header
        H2 title = new H2("Passwort vergessen?");
        title.getStyle().set("textAlign", "center");

        // Description
        Div description = new Div();
        description.setText("Bitte geben Sie Ihre E-Mail an. Sie erhalten dann eine E-Mail mit einem Link, über den Sie ein neues Passwort wählen können.");
        description.getStyle().set("textAlign", "center");
        description.getStyle().set("marginBottom", "20px");

        // Email field
        EmailField emailField = new EmailField();
        emailField.setPlaceholder("E-Mail");
        emailField.setWidthFull();

        // Send button
        Button resetPasswordButton = new Button("Senden", event -> {
            if (emailField.isEmpty()) {
                Notification.show("Das E-Mail-Feld darf nicht leer sein.", 3000, Notification.Position.MIDDLE);
            } else {
                String email = emailField.getValue();
                if (userService.emailExists(email)) {
                    User user = userService.findByUsername(email);
                    PasswordResetToken token = passwordResetService.createToken(user);
                    String resetUrl = createPasswordResetUrl(token.getToken());
                    emailService.sendSimpleMessage(email, "Passwort zurücksetzen", "Hier ist der Link zum Zurücksetzen Ihres Passworts: " + resetUrl);
                    Notification.show("Eine E-Mail zum Zurücksetzen des Passworts wurde versendet.", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate("login"));
                } else {
                    Notification.show("Es gibt keinen Account, der mit dieser E-Mail-Adresse verknüpft ist.", 3000, Notification.Position.MIDDLE);
                }
            }
        });

        // Back to login button
        Button backButton = new Button("Zurück zum Login", event -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        // Layout
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

    private String createPasswordResetUrl(String token) {
        return "http://localhost:8080/reset-password?token=" + token;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Any additional logic to be executed before entering the view
    }
}






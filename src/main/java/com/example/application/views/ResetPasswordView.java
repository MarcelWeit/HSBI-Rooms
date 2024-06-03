package com.example.application.views;

import com.example.application.data.entities.PasswordResetToken;
import com.example.application.services.PasswordResetService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Map;

@PageTitle("Passwort zurücksetzen")
@AnonymousAllowed
@Route(value = "reset-password")
public class ResetPasswordView extends VerticalLayout implements BeforeEnterObserver {

    private final PasswordResetService passwordResetService;
    private final UserService userService;
    private PasswordResetToken token;

    public ResetPasswordView(PasswordResetService passwordResetService, UserService userService) {
        this.passwordResetService = passwordResetService;
        this.userService = userService;
        addClassName("reset-password-view");

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Description
        Div description = new Div();
        description.setText("Bitte geben Sie Ihr neues Passwort ein.");
        description.getStyle().set("textAlign", "center");
        description.getStyle().set("marginBottom", "20px");

        // Password fields
        PasswordField newPasswordField = new PasswordField("Neues Passwort");
        newPasswordField.setWidthFull();

        PasswordField confirmPasswordField = new PasswordField("Passwort wiederholen");
        confirmPasswordField.setWidthFull();

        // Reset password button
        Button resetPasswordButton = new Button("Passwort zurücksetzen", event -> {
            String newPassword = newPasswordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Notification.show("Die Passwortfelder dürfen nicht leer sein.", 3000, Notification.Position.MIDDLE);
            } else if (newPassword.length() < 8) {
                Notification.show("Das Passwort muss mindestens 8 Zeichen lang sein.", 3000, Notification.Position.MIDDLE);
            } else if (!newPassword.equals(confirmPassword)) {
                Notification.show("Die Passwörter stimmen nicht überein.", 3000, Notification.Position.MIDDLE);
            } else {
                if (token != null) {
                    userService.updatePassword(token.getUser(), newPassword);
                    Notification.show("Ihr Passwort wurde erfolgreich zurückgesetzt.", 3000, Notification.Position.MIDDLE);
                    passwordResetService.deleteToken(token);
                    getUI().ifPresent(ui -> ui.navigate("login"));
                } else {
                    Notification.show("Der Link zum zurücksetzen Ihres Passwortes ist abgelaufen.", 3000, Notification.Position.MIDDLE);
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
        formLayout.add(description, newPasswordField, confirmPasswordField, resetPasswordButton, backButton);

        add(formLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
        if (parameters.containsKey("token")) {
            String token = parameters.get("token").get(0); // Nimmt an, dass der Token nur einmal in der URL vorkommt
            this.token = passwordResetService.validateToken(token);
            if (this.token == null) {
                Notification.show("Der Link zum zurücksetzen Ihres Passwortes ist abgelaufen.", 3000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Kein Token empfangen.", 3000, Notification.Position.MIDDLE);
        }

    }
}

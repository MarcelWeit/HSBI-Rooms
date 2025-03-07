package com.example.application.views;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final LoginOverlay loginOverlay;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        addClassName("login-view");
        loginOverlay = new LoginOverlay();

        getElement().getThemeList().add("dark");

        loginOverlay.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        loginOverlay.setForgotPasswordButtonVisible(false);

        setupFooter();
        setupLanguage();

        add(loginOverlay);
        loginOverlay.setOpened(true);
    }

    private void setupFooter() {
        Button registerButton = new Button("Registrieren");
        registerButton.addClickListener(e -> {
            loginOverlay.setOpened(false);
            getUI().ifPresent(ui -> ui.navigate("register"));
        });

        Anchor forgotPasswordLink = new Anchor("forgot-password", "Passwort vergessen?");
        forgotPasswordLink.getElement().addEventListener("click", e -> {
            loginOverlay.setOpened(false);
            getUI().ifPresent(ui -> ui.navigate("forgot-password"));
        });

        Div footer = new Div();
        footer.add(registerButton, forgotPasswordLink);
        footer.getStyle().set("display", "flex");
        footer.getStyle().set("flexDirection", "column");
        footer.getStyle().set("alignItems", "center");
        footer.getStyle().set("width", "100%");
        footer.getStyle().set("gap", "10px"); // Add space between elements

        loginOverlay.getFooter().add(footer);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            loginOverlay.setOpened(false);
            event.forwardTo("");
        }

        loginOverlay.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    public void setupLanguage() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("HSBI Rooms");
        i18n.getHeader().setDescription("Das Raumbuchungstool der HSBI");

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Einloggen");
        i18nForm.setUsername("E-Mail");
        i18nForm.setPassword("Passwort");
        i18nForm.setSubmit("Einloggen");
        i18nForm.setForgotPassword("");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Falsche E-Mail oder Passwort");
        i18nErrorMessage.setMessage("Bitte prüfen Sie Ihre Eingaben");
        i18n.setErrorMessage(i18nErrorMessage);

        i18n.setAdditionalInformation(null);
        loginOverlay.setI18n(i18n);
        loginOverlay.setError(true);
    }


}

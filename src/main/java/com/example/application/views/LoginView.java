package com.example.application.views;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.theme.lumo.LumoUtility;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final LoginOverlay loginOverlay;

    public LoginView(AuthenticatedUser authenticatedUser) {
        addClassName("login-view");
        loginOverlay = new LoginOverlay();
        this.authenticatedUser = authenticatedUser;

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
        registerButton.addClassName(LumoUtility.TextAlignment.CENTER);
        registerButton.addClickListener(e -> {
            loginOverlay.setOpened(false);
            getUI().ifPresent(ui -> ui.navigate("register"));
        });
        loginOverlay.getFooter().add(registerButton);
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
        i18n.getHeader().setTitle("Raumbuchung");
        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);
        loginOverlay.setI18n(i18n);
    }


}

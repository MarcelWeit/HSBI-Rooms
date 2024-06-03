package com.example.application.views;

import com.example.application.data.entities.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * @author Marcel Weithoener
 */
@Route(value = "startseite", layout = MainLayout.class)
@Secured({"DOZENT", "FBPLANUNG", "ADMIN"})
@RolesAllowed({"DOZENT", "FBPLANUNG", "ADMIN"})
@PageTitle("Startseite")
@RouteAlias(value = "", layout = MainLayout.class)
public class Startseite extends VerticalLayout {

    private final UserService userService;

    private final AuthenticatedUser authenticatedUser;

    /**
     * @param userService Service für User
     */
    public Startseite(UserService userService, AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        LocalDate today = LocalDate.now();
        int weekNumber = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        H2 h2;
        if (authenticatedUser.get().isPresent()) {
            User currentUser = authenticatedUser.get().get();
            h2 = new H2("Hallo " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
        } else {
            h2 = new H2("Hallo !");
        }
        H3 h3 = new H3("Herzlich Willkommen bei dem Raumplanungstool der HSBI");

        Span dateSpan = new Span("Heutiges Datum: " + today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        dateSpan.getStyle().set("border", "1px solid white");
        dateSpan.getStyle().set("padding", "10px");

        Span weekSpan = new Span("Kalenderwoche: " + weekNumber);
        weekSpan.getStyle().set("border", "1px solid white");
        weekSpan.getStyle().set("padding", "10px");

        HorizontalLayout horizontalLayout = new HorizontalLayout(dateSpan, weekSpan);

        add(h2, h3, horizontalLayout);
    }

}

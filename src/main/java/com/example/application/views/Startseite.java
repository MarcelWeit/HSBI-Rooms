package com.example.application.views;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Route(value = "startseite", layout = MainLayout.class)
@RolesAllowed({"DOZENT", "FBPLANUNG", "ADMIN"})
@PageTitle("Startseite")
@RouteAlias(value = "", layout = MainLayout.class)
public class Startseite extends VerticalLayout {

    public Startseite() {
        LocalDate today = LocalDate.now();
        int weekNumber = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        H3 h3 = new H3("Herzlich Willkommen bei RoomHSBI, dem Raumplanungstool der HSBI");
        Span dateSpan = new Span("Heutiges Datum: " + today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        dateSpan.getStyle().set("border", "1px solid white");
        dateSpan.getStyle().set("padding", "10px");

        Span weekSpan = new Span("Kalenderwoche: " + weekNumber);
        weekSpan.getStyle().set("border", "1px solid white");
        weekSpan.getStyle().set("padding", "10px");

        HorizontalLayout horizontalLayout = new HorizontalLayout(dateSpan, weekSpan);

        add(h3, horizontalLayout);
    }

}

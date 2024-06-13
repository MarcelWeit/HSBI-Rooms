package com.example.application.views;

import com.example.application.data.entities.Buchung;
import com.example.application.data.entities.User;
import com.example.application.data.enums.Role;
import com.example.application.dialogs.BuchungAnlegenBearbeitenDialog;
import com.example.application.dialogs.BuchungenAnzeigenDialog;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
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
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

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

    private final RaumService raumService;
    private final DozentService dozentService;
    private final BuchungService buchungService;
    private final VeranstaltungService veranstaltungService;

    /**
     * @param userService Service für User
     */
    public Startseite(UserService userService, AuthenticatedUser authenticatedUser, RaumService raumService, DozentService dozentService, BuchungService buchungService, VeranstaltungService veranstaltungService) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.raumService = raumService;
        this.dozentService = dozentService;
        this.buchungService = buchungService;
        this.veranstaltungService = veranstaltungService;
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

        FullCalendar fullCalendar = createCalendar();

        Button buchungAnlegen = new Button("Buchung anlegen", click -> {
            Dialog roomBookDialog = new BuchungAnlegenBearbeitenDialog(null, Optional.empty(), Optional.empty(), raumService, dozentService, buchungService, veranstaltungService,
                    authenticatedUser);
            roomBookDialog.open();
        });

        Button eigeneBuchungen = new Button("Eigene Buchungen", click -> {
            Dialog showBookingsDialog = new BuchungenAnzeigenDialog(Optional.empty(), raumService, dozentService, buchungService, veranstaltungService, authenticatedUser);
            showBookingsDialog.open();
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(dateSpan, weekSpan);
        HorizontalLayout buchungButtons = new HorizontalLayout(buchungAnlegen, eigeneBuchungen);
        HorizontalLayout calenderLayout = new HorizontalLayout(fullCalendar);

        add(h2, h3, horizontalLayout, buchungButtons, calenderLayout);
    }

    public FullCalendar createCalendar() {
        FullCalendar calendar = FullCalendarBuilder.create().build();
        List<Entry> entryList = new LinkedList<>();
        EntryProvider<Entry> entryProvider;

        Set<Buchung> buchungen = new HashSet<>();

        if (authenticatedUser.get().isPresent()) {
            if (authenticatedUser.get().get().getRoles().contains(Role.DOZENT)) {
                buchungen = buchungService.findAllByDozent(dozentService.findByVornameAndNachname(authenticatedUser.get().get().getFirstName(), authenticatedUser.get().get().getLastName()));
            }
        }

        Entry entry = new Entry();
        String start;
        String end;
        if (!buchungen.isEmpty()) {
            for (Buchung buchung : buchungen) {
                entry.setTitle(buchung.getVeranstaltung().toString());
                entry.setDescription(buchung.getRoom().toString());

                switch (buchung.getZeitslot()) {
                    case EINS:
                        start = buchung.getDate().toString().concat("T08:00:00");
                        end = buchung.getDate().toString().concat("T09:30:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    case ZWEI:
                        start = buchung.getDate().toString().concat("T09:45:00");
                        end = buchung.getDate().toString().concat("T11:15:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    case DREI:
                        start = buchung.getDate().toString().concat("T11:30:00");
                        end = buchung.getDate().toString().concat("T13:00:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    case VIER:
                        start = buchung.getDate().toString().concat("T14:00:00");
                        end = buchung.getDate().toString().concat("T15:30:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    case FUENF:
                        start = buchung.getDate().toString().concat("T15:45:00");
                        end = buchung.getDate().toString().concat("T17:15:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    case SECHS:
                        start = buchung.getDate().toString().concat("T17:30:00");
                        end = buchung.getDate().toString().concat("T19:00:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    case SIEBEN:
                        start = buchung.getDate().toString().concat("T19:15:00");
                        end = buchung.getDate().toString().concat("T20:45:00");

                        entry.setStart(LocalDateTime.parse(start));
                        entry.setEnd(LocalDateTime.parse(end));
                        break;
                    default:
                        //Fehlermeldung oder so
                        break;
                }

                entryList.add(entry);

            }
        }

        return calendar;
    }

}

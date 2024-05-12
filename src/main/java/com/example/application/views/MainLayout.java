package com.example.application.views;

import com.example.application.data.entities.User;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        getElement().getThemeList().add("dark");
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Add a button to switch between light and dark mode
        Button themeSwitcher = new Button("Switch Theme", click -> {
            if (getElement().getThemeList().contains("dark")) {
                getElement().getThemeList().remove("dark");
                getElement().getThemeList().add("light");
            } else {
                getElement().getThemeList().remove("light");
                getElement().getThemeList().add("dark");
            }
        });
        themeSwitcher.addClassName("top-right-button");

        addToNavbar(true, toggle, viewTitle, themeSwitcher);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Raumbuchung");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    // Navigation an der linken Seite erstellen
    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if(accessChecker.hasAccess(Startseite.class)){
            nav.addItem(
                    new SideNavItem("Startseite", Startseite.class, VaadinIcon.HOME.create()));
        }

        // Kopf Navigation Verwaltung mit Unterpunkten
        SideNavItem verwNav = new SideNavItem("Verwaltung");
        if(accessChecker.hasAccess(AusstattungView.class)){
            verwNav.addItem(
                    new SideNavItem("Ausstattung", AusstattungView.class, VaadinIcon.TABLE.create()));
        }
        if(accessChecker.hasAccess(RoomCrud.class)){
            verwNav.addItem(
                    new SideNavItem("Raum", RoomCrud.class, VaadinIcon.TABLE.create()));
        }


        verwNav.setExpanded(true);
        nav.addItem(verwNav);

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getFirstName());
//            StreamResource resource = new StreamResource("profile-pic",
//                    () -> new ByteArrayInputStream(user.getProfilePicture()));
//            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getFirstName() + " " + user.getLastName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e ->
                authenticatedUser.logout()
            );

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

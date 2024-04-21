package com.example.application.views;

import com.example.application.data.entities.Ausstattung;
import com.example.application.services.AusstattungService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Add Ausstattung")
@Route(value = "add-ausstattung", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AddAusstattungView extends VerticalLayout {

    private final AusstattungService ausstattungService;

    public AddAusstattungView(AusstattungService ausstattungService) {
        addClassNames("add-ausstattung");
        this.ausstattungService = ausstattungService;
        createComponents();
    }

    private void createComponents() {
        TextField bez = new TextField("Bezeichnung");
        Button create = new Button("Create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(bez, create);

        create.addClickListener(e -> {
            if(ausstattungService.existsByBez(bez.getValue())) {
                bez.setErrorMessage("Ausstattung existiert bereits");
                bez.setInvalid(true);
            } else {
                Ausstattung newAusstattung = new Ausstattung();
                newAusstattung.setBez(bez.getValue());
                ausstattungService.update(newAusstattung);
                bez.clear();
                Notification.show("Ausstattung erstellt", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);;
            }
        });
    }

}

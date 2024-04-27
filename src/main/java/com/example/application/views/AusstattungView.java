package com.example.application.views;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.repository.RoomRepository;
import com.example.application.services.AusstattungService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Set;

/**
 *
 * @author: marcel weithoener
 *
 */

//@Todo: Ausstattung löschbar machen, wenn in Raum verwendet

@Route(value = "show-ausstattung", layout = MainLayout.class)
@PageTitle("Ausstattungen anzeigen")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class AusstattungView extends VerticalLayout {

    private final AusstattungService ausstattungService;
    private final RoomRepository roomRepository;

    private Grid<Ausstattung> grid;

    public AusstattungView(AusstattungService ausstattungService, RoomRepository roomRepository) {
        addClassNames("ausstattung-view");
        this.ausstattungService = ausstattungService;
        this.roomRepository = roomRepository;
        createAddButton();
        createGrid();
    }

    private void createAddButton() {
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setAlignItems(Alignment.BASELINE);
        TextField bez = new TextField("Bezeichnung");
        Button addButton = new Button("Hinzufügen");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            if(ausstattungService.existsByBez(bez.getValue())) {
                bez.setErrorMessage("Ausstattung existiert bereits");
                bez.setInvalid(true);
            } else if(bez.getValue().isEmpty()){
                bez.setErrorMessage("Bitte geben Sie eine Bezeichnung ein");
                bez.setInvalid(true);
            } else {
                Ausstattung newAusstattung = new Ausstattung();
                newAusstattung.setBez(bez.getValue());
                ausstattungService.update(newAusstattung);
                grid.setItems(ausstattungService.findAll());
                bez.clear();
            }
        });

        addLayout.add(bez, addButton);
        add(addLayout);
    }

    private void createGrid() {
        grid = new Grid<>();
        Set<Ausstattung> rooms = ausstattungService.findAll();
        grid.setItems(rooms);

        grid.addColumn(Ausstattung::getBez).setHeader("Bezeichnung");
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, ausstattung) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
                Dialog dialog = new Dialog();
                dialog.setCloseOnEsc(false);

                int roomCount = roomRepository.countByAusstattungContains(ausstattung);

                HorizontalLayout buttonLayout = new HorizontalLayout();
                Text message;
                if(roomCount > 0) {
                    message = new Text("Diese Ausstattung wird in " + roomCount + " Räumen verwendet. Bitte löschen Sie zuerst die Räume, die diese Ausstattung verwenden.");
                    Button verstandenButton = new Button("Verstanden", event -> dialog.close());
                    buttonLayout.add(verstandenButton);
                } else {
                    message = new Text("Diese Ausstattung wird in " + roomCount + " Räumen verwendet. Sind Sie sicher, dass Sie diese Ausstattung löschen möchten?");
                    dialog.setCloseOnOutsideClick(false);

                    Button confirmButton = new Button("Bestätigen", event -> {
                        ausstattungService.delete(ausstattung);
                        grid.setItems(ausstattungService.findAll());
                        dialog.close();
                    });

                    Button cancelButton = new Button("Abbrechen", event -> dialog.close());
                    buttonLayout.add(confirmButton, cancelButton);
                }

                dialog.add(message, buttonLayout);

                // Open the dialog
                dialog.open();
            });
            button.setIcon(new Icon(VaadinIcon.TRASH));
        })).setHeader("Löschen");

        GridPro<String> gridpro = new GridPro<>();

        add(grid, gridpro);
    }

}

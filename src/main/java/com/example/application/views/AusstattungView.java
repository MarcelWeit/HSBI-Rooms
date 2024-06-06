package com.example.application.views;

import com.example.application.data.entities.Ausstattung;
import com.example.application.data.entities.Raum;
import com.example.application.services.AusstattungService;
import com.example.application.services.RaumService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
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
 * @author Marcel Weithoener
 */

@Route(value = "show-ausstattung", layout = MainLayout.class)
@PageTitle("Ausstattung")
@RolesAllowed({"ADMIN", "FBPLANUNG"})
@Uses(Icon.class)
public class AusstattungView extends VerticalLayout {

    private final AusstattungService ausstattungService;
    private final RaumService roomService;

    private Grid<Ausstattung> grid;

    public AusstattungView(AusstattungService ausstattungService, RaumService roomService) {
        addClassNames("ausstattung-view");
        this.ausstattungService = ausstattungService;
        this.roomService = roomService;
        createAddButton();
        createGrid();
        grid.setHeight("70vh");
    }

    private void createAddButton() {
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setAlignItems(Alignment.BASELINE);
        TextField bez = new TextField("Bezeichnung");
        Button addButton = new Button("Hinzufügen");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
            if (ausstattungService.existsByBezEqualsIgnoreCase(bez.getValue())) {
                bez.setErrorMessage("Ausstattung existiert bereits");
                bez.setInvalid(true);
            } else if (bez.getValue().isEmpty()) {
                bez.setErrorMessage("Bitte geben Sie eine Bezeichnung ein");
                bez.setInvalid(true);
            } else {
                Ausstattung newAusstattung = new Ausstattung();
                newAusstattung.setBez(bez.getValue());
                ausstattungService.save(newAusstattung);
                grid.setItems(ausstattungService.findAll());
                bez.clear();
            }
        });
        addButton.addClickShortcut(Key.ENTER);


        addLayout.add(bez, addButton);
        add(addLayout);
    }

    private void createGrid() {
        grid = new Grid<>();
        Set<Ausstattung> rooms = ausstattungService.findAll();
        grid.setItems(rooms);

        Grid.Column<Ausstattung> bezColumn = grid.addColumn(Ausstattung::getBez).setHeader("Bezeichnung").setAutoWidth(true).setFlexGrow(0).setResizable(true);
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, ausstattung) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> openDialog(ausstattung));
            button.setIcon(new Icon(VaadinIcon.TRASH));
        })).setHeader("Löschen");

        // Sortierung nach Bezeichnung aufsteigend alphabetisch
        grid.sort(GridSortOrder.asc(bezColumn).build());

        add(grid);
    }

    private void openDialog(Ausstattung ausstattung) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);

        int roomCount = roomService.countByAusstattungContains(ausstattung);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Text message = roomCount == 1 ? new Text("Diese Ausstattung wird in einem Raum verwendet. Sind Sie sicher, dass Sie diese Ausstattung löschen möchten?")
                : roomCount == 0 ? new Text("Diese Ausstattung wird in keinem Raum verwendet. Sind Sie sicher, dass Sie diese Ausstattung löschen möchten?")
                : new Text("Diese Ausstattung wird in " + roomCount + " " + "Räumen verwendet. Sind Sie sicher, dass Sie diese Ausstattung löschen möchten?");
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Bestätigen", event -> {

            Set<Raum> roomsWithAusstattung = roomService.findAllByAusstattungContains(ausstattung);
            roomsWithAusstattung.forEach(room -> {
                room.removeAusstattung(ausstattung);
                roomService.save(room);
            });
            ausstattungService.delete(ausstattung);

            grid.setItems(ausstattungService.findAll());
            dialog.close();
        });

        Button cancelButton = new Button("Abbrechen", event -> dialog.close());
        buttonLayout.add(confirmButton, cancelButton);

        dialog.add(message, buttonLayout);

        dialog.open();
    }

}

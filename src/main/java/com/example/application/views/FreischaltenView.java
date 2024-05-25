package com.example.application.views;

import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;

@Route(value = "freischalten", layout = MainLayout.class)
@PageTitle("User Approval")
@Secured("ADMIN")
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class FreischaltenView extends VerticalLayout {

    private final UserService userService;
    private final Grid<User> lockedUsersGrid;

    public FreischaltenView(UserService userService) {
        this.userService = userService;

        // Locked Users Grid for Admin Approval
        lockedUsersGrid = new Grid<>(User.class);
        setupLockedUsersGrid();

        add(lockedUsersGrid);
    }

    private void setupLockedUsersGrid() {
        lockedUsersGrid.setItems(userService.findLockedUsers());
        lockedUsersGrid.removeColumn(lockedUsersGrid.getColumnByKey("id"));

        lockedUsersGrid.removeColumn(lockedUsersGrid.getColumnByKey("hashedPassword"));
        lockedUsersGrid.removeColumn(lockedUsersGrid.getColumnByKey("locked"));




        // Add the approve button column
        lockedUsersGrid.addComponentColumn(user -> {
            Button approveButton = new Button("Approve", event -> {
                user.setLocked(false);
                userService.save(user);
                refreshLockedUsersGrid();
                Notification.show("User approved", 3000, Notification.Position.MIDDLE);
            });
            return approveButton;
        }).setHeader("Actions");
    }

    private void refreshLockedUsersGrid() {
        lockedUsersGrid.setItems(userService.findLockedUsers());
    }
}




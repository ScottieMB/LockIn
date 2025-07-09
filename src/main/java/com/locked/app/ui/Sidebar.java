package com.locked.app.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import com.locked.app.controllers.AppController;
import com.locked.app.models.UserSession;
import com.locked.app.util.ImageUtil;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {
    
    public Sidebar(UserSession user, AppController controller) {
        super();
        // in case of desire for easy expansion
        LinkedHashMap<String, FontIcon> options = new LinkedHashMap<>();

        FontIcon mainIcon = new FontIcon("fth-edit-2");
        FontIcon appIcon = new FontIcon("fth-smartphone");
        FontIcon musicIcon = new FontIcon("fth-headphones");

        options.put("lock-in", mainIcon);
        options.put("app permissions", appIcon);
        options.put("playlists", musicIcon);

        Button pfpImage = ImageUtil.setupProfilePicture(user);
        pfpImage.getStyleClass().add("sidebar-button");

        Region topSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);

        Region botSpacer = new Region();
        VBox.setVgrow(botSpacer, Priority.ALWAYS);

        this.getChildren().add(pfpImage);
        this.getChildren().add(new Separator(Orientation.HORIZONTAL));
        this.getChildren().add(topSpacer);

        for (Map.Entry<String, FontIcon> entry : options.entrySet()) {
            String label = entry.getKey();
            FontIcon icon = entry.getValue();
            Button b = new Button(label, icon);
            b.getStyleClass().add("sidebar-button");

            b.setOnAction(_ -> {
                switch (label) {
                    case "lock-in" -> controller.goToMain(user);
                    case "app permissions" -> controller.goToAppPermissions(user);
                    case "playlists" -> controller.goToPlaylists(user);
                    default -> System.out.println("No action defined for: " + label);
                }
            });

            this.getChildren().add(b);
        }

        this.getChildren().add(botSpacer);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("sidebar");
    }
}
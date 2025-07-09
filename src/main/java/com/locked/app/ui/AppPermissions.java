package com.locked.app.ui;

import java.io.File;

import com.locked.app.controllers.AppController;
import com.locked.app.models.UserSession;
import com.locked.app.services.ProcessManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppPermissions {
    private AppController controller;
    private Stage stage;
    private UserSession user;
    private ProcessManager processManager;

    public AppPermissions(AppController controller, Stage stage, UserSession user, ProcessManager processManager) {
        this.controller = controller;
        this.stage = stage;
        this.user = user;
        this.processManager = processManager;
    }

    private void createWindow() {
        BorderPane root = new BorderPane();
        ListView<File> table = new ListView<File>();
        Label label = new Label("your blacklisted applications");

        // add files to the observablelist
        Button button = new Button("select an application");
        button.getStyleClass().add("button-main");
        button.setOnAction(_ -> {
            processManager.addAppToBlacklist(stage);
            System.out.println(processManager.getBlacklistedApps());
        });

        // set the items to the observablelist
        table.setItems(processManager.getBlacklistedApps());

        Sidebar side = new Sidebar(user, controller);

        VBox centerPiece = new VBox(10, label, table, button);
        centerPiece.setAlignment(Pos.CENTER);
        
        root.setLeft(side);
        root.setCenter(centerPiece);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("App Permissions");
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
    }

    public void show() {
        createWindow();
        stage.show();
    }
}
package com.locked.app.ui;

import com.locked.app.controllers.AppController;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class StartWindow {
    private Stage stage;
    private AppController controller;
    
    public StartWindow(AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        createScene();
    }
    
    public void createScene() {
        // Create components
        Label header = new Label("study better.");
        Label title = new Label("lock in.");
        Button signUpBtn = new Button("sign up");
        Button loginBtn = new Button("log in");
        
        // Apply styling
        header.getStyleClass().add("label-main");
        title.getStyleClass().add("label-title");
        signUpBtn.getStyleClass().add("button-main");
        loginBtn.getStyleClass().add("button-main");
        
        // Create layouts
        HBox topHeader = createTopHeader(header);
        VBox centerTitle = createCenterTitle(title);
        HBox bottomButtons = createBottomButtons(signUpBtn, loginBtn);
        
        // Setup animations
        setupAnimations(topHeader, centerTitle, bottomButtons);
        
        // Setup event handlers
        signUpBtn.setOnAction(_ -> controller.goToSignUp());
        loginBtn.setOnAction(_ -> controller.goToLogin());
        
        // Main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");
        root.setTop(topHeader);
        root.setCenter(centerTitle);
        root.setBottom(bottomButtons);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Lock In");
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
    }
    
    private HBox createTopHeader(Label header) {
        HBox topHeader = new HBox(header);
        topHeader.setAlignment(Pos.CENTER);
        topHeader.setStyle("-fx-padding: 20;");
        topHeader.setOpacity(0);
        return topHeader;
    }
    
    private VBox createCenterTitle(Label title) {
        VBox centerTitle = new VBox(title);
        centerTitle.setAlignment(Pos.CENTER);
        centerTitle.setFillWidth(true);
        centerTitle.setOpacity(0);
        return centerTitle;
    }
    
    private HBox createBottomButtons(Button signUp, Button logIn) {
        HBox bottomButtons = new HBox(20, signUp, logIn);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setStyle("-fx-padding: 20;");
        bottomButtons.setOpacity(0);
        return bottomButtons;
    }
    
    private void setupAnimations(HBox topHeader, VBox centerTitle, HBox bottomButtons) {
        FadeTransition headerFade = new FadeTransition(Duration.seconds(2), topHeader);
        headerFade.setFromValue(0);
        headerFade.setToValue(1);
        
        FadeTransition titleFade = new FadeTransition(Duration.seconds(2), centerTitle);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);

        FadeTransition buttonFade = new FadeTransition(Duration.seconds(2), bottomButtons);
        buttonFade.setFromValue(0);
        buttonFade.setToValue(1);
        
        headerFade.setOnFinished(_ -> titleFade.play());
        titleFade.setOnFinished(_ -> buttonFade.play());
        headerFade.play();
    }
    
    public void show()
    {
        stage.show();
    }
}
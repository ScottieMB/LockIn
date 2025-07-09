package com.locked.app.ui;

import com.locked.app.auth.Delegator;
import com.locked.app.controllers.AppController;
import com.locked.app.models.UserSession;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class LoginWindow {
    private Stage loginStage;
    private AppController controller;
    private Delegator delegator;
    
    public LoginWindow(AppController controller, Delegator delegator) {
        this.controller = controller;
        this.delegator = delegator;
        this.loginStage = new Stage();
        setupWindow();
    }
    
    private void setupWindow() {
        Label titleLabel = new Label("log in");
        Label usernameLabel = new Label("username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("password:");
        PasswordField passwordField = new PasswordField();
        Label errorLabel = new Label("");
        Button submitBtn = new Button("log in");
        
        // Apply styling
        titleLabel.getStyleClass().add("label-main");
        usernameLabel.getStyleClass().add("label-main");
        passwordLabel.getStyleClass().add("label-main");
        usernameField.getStyleClass().add("text-field");
        errorLabel.getStyleClass().add("label-error");
        submitBtn.getStyleClass().add("button-main");
        
        // Setup prompts
        usernameField.setPromptText("enter username");
        passwordField.setPromptText("enter password");
        
        // Setup event handler
        submitBtn.setOnAction(_ -> handleLogin(usernameField, passwordField, errorLabel));
        
        // Layout
        VBox root = new VBox(10, errorLabel, titleLabel, usernameLabel, usernameField,
                            passwordLabel, passwordField, submitBtn);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white");
        root.setPadding(new Insets(20));
        
        Scene scene = new Scene(root, 320, 300);
        loginStage.setScene(scene);
        loginStage.setTitle("Log In");
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
    }
    
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label errorLabel) {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getText().toCharArray();
        
        try {
            // Attempt login
            if (delegator.login(username, password)) {
                BufferedImage pfp = delegator.getUserProfilePicture(username);
                UserSession user = new UserSession(username, pfp);
                loginStage.close();
                controller.goToMain(user);
                clearFields(usernameField, passwordField, errorLabel);
            } else {
                errorLabel.setText("incorrect username or password.");
            }
        } catch (Exception e) {
            errorLabel.setText("login failed. please try again.");
            e.printStackTrace();
        } finally {
            Arrays.fill(password, '\0');
        }
    }
    
    private void clearFields(TextField usernameField, PasswordField passwordField, Label errorLabel) {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setText("");
    }
    
    public void show() {
        loginStage.show();
    }
}
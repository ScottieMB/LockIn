package com.locked.app.ui;

import com.locked.app.auth.Delegator;
import com.locked.app.controllers.AppController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import java.util.Arrays;

public class SignUpWindow {
    private Stage signUpStage;
    private AppController controller;
    private Delegator delegator;
    
    // stage constructor
    public SignUpWindow(AppController controller, Delegator delegator) {
        this.controller = controller;
        this.delegator = delegator;
        this.signUpStage = new Stage();
        setupWindow();
    }
    
    // Sets up the window for the login
    private void setupWindow() {
        Label titleLabel = new Label("Sign Up");
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Label errorLabel = new Label("");
        Button submitBtn = new Button("Sign Up");
        
        // Apply styling
        titleLabel.getStyleClass().add("label-main");
        usernameLabel.getStyleClass().add("label-main");
        passwordLabel.getStyleClass().add("label-main");
        confirmPasswordLabel.getStyleClass().add("label-main");
        usernameField.getStyleClass().add("text-field");
        errorLabel.getStyleClass().add("label-error");
        submitBtn.getStyleClass().add("button-main");
        
        // Setup prompts
        usernameField.setPromptText("Choose a username");
        passwordField.setPromptText("Choose a password");
        confirmPasswordField.setPromptText("Confirm your password");
        
        // Setup event handler
        submitBtn.setOnAction(_ -> handleSignUp(usernameField, passwordField, confirmPasswordField, errorLabel));
        
        // Layout
        VBox root = new VBox(10, errorLabel, titleLabel, usernameLabel, usernameField,
                            passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, submitBtn);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white");
        root.setPadding(new Insets(20));
        
        Scene scene = new Scene(root, 320, 350);
        signUpStage.setScene(scene);
        signUpStage.setTitle("Sign Up");
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
    }
    
    private void handleSignUp(TextField usernameField, PasswordField passwordField, 
                             PasswordField confirmPasswordField, Label errorLabel) {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getText().toCharArray();
        char[] confirmPassword = confirmPasswordField.getText().toCharArray();
        
        try {
            // Validate passwords match
            if (!Arrays.equals(password, confirmPassword)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }
            
            // Attempt registration
            if (delegator.register(username, password)) {
                controller.goToStart();
                signUpStage.close();
                clearFields(usernameField, passwordField, confirmPasswordField, errorLabel);
            } else {
                errorLabel.setText("Username already exists.");
            }
        } catch (Exception e) {
            errorLabel.setText("Registration failed. Please try again.");
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
        }
    }
    
    private void clearFields(TextField usernameField, PasswordField passwordField, 
                           PasswordField confirmPasswordField, Label errorLabel) {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        errorLabel.setText("");
    }
    
    public void show() {
        signUpStage.show();
    }
}
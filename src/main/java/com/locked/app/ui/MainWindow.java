package com.locked.app.ui;

import java.awt.image.BufferedImage;
import com.locked.app.controllers.AppController;
import com.locked.app.models.SelectedPlaylist;
import com.locked.app.models.UserSession;
import com.locked.app.services.AppSessionManager;
import com.locked.app.util.ImageUtil;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException;

public class MainWindow {
    private AppController controller;
    private Stage stage;
    private UserSession user;
    private AppSessionManager session;

    public MainWindow(AppController controller, Stage stage, UserSession user, AppSessionManager session) {
        this.controller = controller;
        this.stage = stage;
        this.user = user;
        this.session = session;
    }

    private void createMainWindow() {
        // Setup header (top)
        Label header = new Label("ready to lock in?");

        // Setup configuration (middle)
        HBox currConfig = new HBox(30);
        VBox currPlaylist = new VBox(10);
        Label playlistLabel = new Label("your current playlist");
        Label warning = new Label();
        currPlaylist.getChildren().add(playlistLabel);
        currPlaylist.getStyleClass().add("config-box");
        SelectedPlaylist currentPlaylistDetails = session.getSelectedPlaylist();
        setupMusic(currPlaylist, currentPlaylistDetails, warning);
        currPlaylist.setAlignment(Pos.CENTER);
        currConfig.setAlignment(Pos.CENTER);
        currConfig.getChildren().add(currPlaylist);

        // Setup start (bottom)
        VBox centerPiece = new VBox(10);
        Label timePrompt = new Label();
        TextField timeInput = new TextField();
        setupStudyTime(timePrompt, timeInput);
        Button begin = new Button("begin");
        beginStudying(begin, timeInput, currentPlaylistDetails, warning);
        
        Separator separator1 = new Separator(Orientation.HORIZONTAL);
        Separator separator2 = new Separator(Orientation.HORIZONTAL);

        // Piece it all together
        centerPiece.setAlignment(Pos.CENTER);
        centerPiece.getChildren().addAll(header, begin, separator1, currConfig, separator2, timePrompt, timeInput);

        // Setup sidebar
        Sidebar side = new Sidebar(user, controller);
        BorderPane root = new BorderPane();
        root.setLeft(side);
        root.setCenter(centerPiece);

        // Layout
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Lock In");
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
    }

    // Helper function for playlist display
    private void setupMusic(VBox currPlaylist, SelectedPlaylist currentPlaylistDetails, Label warning) {
        Label playlistStatus = new Label();

        if (session.getMusicService() == null) {
            playlistStatus.setText("no music service selected");
            currPlaylist.getChildren().add(playlistStatus);
        } else if (currentPlaylistDetails == null) {
            playlistStatus.setText("no playlist selected");
            currPlaylist.getChildren().add(playlistStatus);
        } else {
            String name = currentPlaylistDetails.getName();
            Label displayName = new Label(name);
            BufferedImage coverBuffer = currentPlaylistDetails.getImage();
            ImageView cover = ImageUtil.displayCover(coverBuffer);
            warning.setText("remember to open spotify");
            currPlaylist.getChildren().addAll(displayName, cover, warning);
        }
    }
    
    private void setupStudyTime(Label timePrompt, TextField timeInput) {
        timePrompt.setText("how many minutes do you want to study?");
        timeInput.setMaxSize(100, 10);
        timeInput.setAlignment(Pos.CENTER);
        timeInput.setPromptText("e.g. 60"); 
    }

    // Helper function for button functionality
    private void beginStudying(Button begin, TextField timeInput, SelectedPlaylist currentPlaylistDetails, Label warning) {
        begin.setOnAction(_ -> {
            try {
                int studyMins = Integer.parseInt(timeInput.getText().trim());

                if(studyMins <= 0) {
                    throw new NumberFormatException();
                }

                session.setDuration(studyMins);

                if(currentPlaylistDetails == null) {
                    controller.goToLockedIn();
                    return;
                }

                String uri = currentPlaylistDetails.getUri();
                session.getMusicService().playPlaylist(uri);
                controller.goToLockedIn();
            } catch (NotFoundException e) {
                warning.getStyleClass().add("label-error");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        begin.getStyleClass().add("button-main");
    }

    public void show() {
        createMainWindow();
        stage.show();
    }
}
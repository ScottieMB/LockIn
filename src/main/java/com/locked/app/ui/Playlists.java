package com.locked.app.ui;

import javafx.scene.control.Button;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.locked.app.controllers.AppController;
import com.locked.app.models.PlaylistDetails;
import com.locked.app.models.SelectedPlaylist;
import com.locked.app.models.UserSession;
import com.locked.app.services.AppSessionManager;
import com.locked.app.services.SpotifyService;
import com.locked.app.util.ImageUtil;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Playlists {
    private AppController controller;
    private Stage stage;
    private UserSession user;
    private AppSessionManager session;

    public Playlists(AppController controller, Stage stage, UserSession user, AppSessionManager session) {
        this.controller = controller;
        this.stage = stage;
        this.user = user;
        this.session = session;
    }

    private void createPlaylistWindow()
    {
        VBox initialPage = new VBox();
        Label l = new Label("choose a platform:");
        initialPage.getChildren().add(l);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("label-error");

        Button spotify = new Button("Spotify");
        Button apple = new Button("Apple");
        spotify.getStyleClass().add("button-main");
        apple.getStyleClass().add("button-main");
        HBox choices = new HBox(10, spotify, apple);
        initialPage.getChildren().add(choices);

        choices.setAlignment(Pos.CENTER);
        initialPage.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();

        VBox side = new Sidebar(user, controller);
        root.setLeft(side);

        // display based on our signed in status
        if(session.getIsSignedIn() && session.getPlaylistView() != null) {
            GridPane grid = displayPlaylists(session.getPlaylistView());
            root.setCenter(grid);
        } else {
            root.setCenter(initialPage);
        }

        spotify.setOnAction(_ -> {
            Task<PlaylistDetails> handleAuth = new Task<PlaylistDetails>() {
                @Override
                public PlaylistDetails call() throws Exception {
                    session.setMusicService(new SpotifyService());
                    if (session.getMusicService().authenticate()) {
                        session.setIsSignedIn(true);
                    }
                    PlaylistDetails playlists = session.getMusicService().fetchUserPlaylists();
                    session.setPlaylistView(playlists);

                    return playlists;
                }
            };

            handleAuth.setOnSucceeded(_ -> {
                PlaylistDetails playlists = handleAuth.getValue();
                initialPage.setVisible(false);
                GridPane grid = displayPlaylists(playlists);
                root.setCenter(grid);
            });

            handleAuth.setOnFailed(_ -> {
                errorLabel.setText("an error occurred. please try again later.");
                initialPage.getChildren().add(errorLabel);
            });

            new Thread(handleAuth).start();
        });

        apple.setOnAction(_ -> {
            errorLabel.setText("not implemented yet. try Spotify!");
            initialPage.getChildren().add(errorLabel);
        });

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Playlists");
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
    }

    private GridPane displayPlaylists(PlaylistDetails playlists) {
        GridPane grid = new GridPane();
        ArrayList<BufferedImage> images = playlists.getPlaylistImages();
        ArrayList<String> names = playlists.getPlaylistNames();
        ArrayList<String> uris = playlists.getPlaylistURIs();

        if (images.isEmpty() || names.isEmpty()) {
            Label empty = new Label("No playlists found.");
            grid.add(empty, 0, 0);
            grid.setAlignment(Pos.CENTER);
            return grid;
        }

        // main loop to display our playlist details
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Label displayName = new Label(name);
            displayName.setMaxWidth(130);
            displayName.setWrapText(true);

            BufferedImage bImage = images.get(i);

            String uri = uris.get(i);
            Button select = new Button("select");
            select.getStyleClass().add("button-main");
            select.setOnAction(_ -> { 
                SelectedPlaylist s = new SelectedPlaylist(name, bImage, uri);
                session.setSelectedPlaylist(s);
                controller.goToMain(user);
            });

            if (bImage != null) {
                ImageView playlistCover = ImageUtil.displayCover(bImage);
                playlistCover.setFitHeight(100);
                playlistCover.setFitWidth(100);
                playlistCover.getStyleClass().add("cover");

                VBox vbox = new VBox(displayName, playlistCover, select);
                vbox.setSpacing(10);
                vbox.setAlignment(Pos.CENTER);
                vbox.getStyleClass().add("config-box");
                grid.add(vbox, i % 3, i / 3);
            } else {
                VBox vbox = new VBox(new Label("No image"), displayName);
                vbox.setSpacing(10);
                vbox.setAlignment(Pos.CENTER);
                vbox.getStyleClass().add("config-box");
                grid.add(vbox, i % 3, i / 3);
            }
        }
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(30);
        grid.setVgap(30);
        return grid;
    }

    public void show() {
        createPlaylistWindow();
        stage.show();
    }
}
package com.locked.app.ui;

import com.locked.app.controllers.AppController;
import com.locked.app.services.AppSessionManager;
import com.locked.app.services.ProcessManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class LockedInWindow {
    private AppController controller;
    private Stage stage;
    private AppSessionManager session;
    private final ProcessManager processManager;

    public LockedInWindow(AppController controller, Stage stage, AppSessionManager session, ProcessManager processManager) {
        this.controller = controller;
        this.stage = stage;
        this.session = session;
        this.processManager = processManager;
        createWindow();
    }

    private void createWindow() {
        BorderPane root = new BorderPane();
        VBox container = new VBox();
        Label tag = new Label("begin.");
        Label countDownTimer = new Label();
        Label blacklistedCounter = new Label();
        blacklistedCounter.getStyleClass().add("label-warning");

        startMainTimeline(countDownTimer, blacklistedCounter);

        container.getChildren().addAll(tag, countDownTimer, blacklistedCounter);
        container.setAlignment(Pos.CENTER);
        root.setCenter(container);
        root.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Scene scene = new Scene(root, 1000, 700);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setResizable(false);
        scene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());

        handleExitAttempt();
    }

    private void startMainTimeline(Label time, Label blacklistedCounter) {
        int studySeconds = session.getDuration() * 60;
        int[] timerContainer = new int[] {studySeconds};

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            startTimer(time, timerContainer);

            // run on a separate thread
            Task<Void> task = processManager.getProcessCheckTask(() ->
                blacklistedCounter.setText("destroyed " + processManager.getDestroyedAppCount() + " blacklisted applications."));
            new Thread(task).start();
        }));
        
        timeline.setCycleCount(studySeconds);
        timeline.play();
    }

    private void startTimer(Label time, int[] timerContainer) {
        timerContainer[0]--;

        int hrs = timerContainer[0] / 60 / 60;
        int mins = timerContainer[0] / 60;
        int secs = timerContainer[0] % 60;

        time.setText(String.format("%02d:%02d:%02d", hrs, mins, secs));
    }

    private void handleExitAttempt() {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                window.consume();
                VBox vbox = new VBox(10);
                HBox hbox = new HBox(10);
                Stage exitStage = new Stage();
                Label exitLabel = new Label("are you sure you want to exit?");
                Button no = new Button("no");
                Button yes = new Button("yes");
                yes.getStyleClass().add("button-main");
                no.getStyleClass().add("button-main");
                
                yes.setOnAction(_ -> {
                    System.exit(0);
                });

                no.setOnAction(_ -> {
                    exitStage.close();
                });

                hbox.getChildren().addAll(yes, no);
                vbox.getChildren().addAll(exitLabel, hbox);
                hbox.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                
                Scene exitScene = new Scene(vbox, 300, 300);
                exitStage.setScene(exitScene);
                exitStage.setAlwaysOnTop(true);
                exitStage.show();
                exitScene.getStylesheets().add(getClass().getResource("/styles.fx.css").toExternalForm());
            }
        });
    }

    public void show() {
        stage.show();
    }
}
package com.locked.app;

import java.sql.Connection;
import java.sql.SQLException;

import com.locked.app.controllers.AppController;
import com.locked.app.db.DBConnection;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LockIn extends Application {   
    private static DBConnection dbconn;

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/RobotoMono-Regular.ttf"), 14);
        AppController controller = new AppController(stage, dbconn);
        controller.goToStart();
    }

    public static void main(String[] args) {
        dbconn = new DBConnection();
        try (Connection conn = dbconn.getConnection())
        {
            System.out.println("Connected!");
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        launch();
    }
}
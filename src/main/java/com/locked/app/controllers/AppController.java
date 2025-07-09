package com.locked.app.controllers;

import com.locked.app.auth.Delegator;
import com.locked.app.db.DBConnection;
import com.locked.app.models.UserSession;
import com.locked.app.services.ProcessManager;
import com.locked.app.services.AppSessionManager;
import com.locked.app.ui.AppPermissions;
import com.locked.app.ui.LockedInWindow;
import com.locked.app.ui.LoginWindow;
import com.locked.app.ui.MainWindow;
import com.locked.app.ui.Playlists;
import com.locked.app.ui.SignUpWindow;
import com.locked.app.ui.StartWindow;

import javafx.stage.Stage;

public class AppController {
    private Delegator delegator;

    private Stage primaryStage;
    private StartWindow startWindow;
    private LoginWindow loginWindow;
    private SignUpWindow signUpWindow;
    private MainWindow mainWindow;
    private Playlists playlistWindow;
    private LockedInWindow lockedInWindow;
    private AppPermissions appPermissionWindow;

    private AppSessionManager sessionManager;
    private ProcessManager processManager;

    public AppController(Stage primaryStage, DBConnection dbconn) {
        this.primaryStage = primaryStage;
        this.sessionManager = new AppSessionManager();
        this.processManager = new ProcessManager();
        this.delegator = new Delegator(dbconn);
        initializeWindows();
    }

    // Pre-process windows for quick loading
    private void initializeWindows() {
        startWindow = new StartWindow(this, primaryStage);
        loginWindow = new LoginWindow(this, delegator);
        signUpWindow = new SignUpWindow(this, delegator);
    }

    public void goToStart() {
        startWindow.show();
    }

    public void goToLogin() {
        loginWindow.show();
    }

    public void goToSignUp() {
        signUpWindow.show();
    }

    // make the following singletons since they're determined at runtime, we don't want it to be instantiated more than once
    public void goToMain(UserSession user) {
        if (mainWindow == null) {
            mainWindow = new MainWindow(this, primaryStage, user, sessionManager);
        }
        mainWindow.show();
    }

    public void goToAppPermissions(UserSession user) {
        if (appPermissionWindow == null) {
            appPermissionWindow = new AppPermissions(this, primaryStage, user, processManager);
        }
        appPermissionWindow.show();
    }

    public void goToPlaylists(UserSession user) {
        if (playlistWindow == null) {
            playlistWindow = new Playlists(this, primaryStage, user, sessionManager);
        }
        playlistWindow.show();
    }

    // public void goToProfile() {

    // }

    public void goToLockedIn() {
        if (lockedInWindow == null) {
            lockedInWindow = new LockedInWindow(this, primaryStage, sessionManager, processManager);
        }
        lockedInWindow.show();
    }
}

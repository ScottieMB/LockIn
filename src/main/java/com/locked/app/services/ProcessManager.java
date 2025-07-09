package com.locked.app.services;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProcessManager {
    private ObservableList<File> blacklistedApps;
    private Map<File, ArrayList<ProcessHandle>> appsByGrouping;
    private ArrayList<ArrayList<ProcessHandle>> appsToKill;
    private AtomicInteger destroyedAppCount = new AtomicInteger(0);

    public ProcessManager() {
        blacklistedApps = FXCollections.observableArrayList();
        appsByGrouping = new HashMap<>();
        appsToKill = new ArrayList<>();
    }

    // run it on a separate thread
    public Task<Void> getProcessCheckTask(Runnable onUiUpdate) {
        return new Task<>() {
            @Override
            protected Void call() {
                killBlacklistedApplications();
                if (onUiUpdate != null) {
                    Platform.runLater(onUiUpdate);
                }
                return null;
            }
        };
    }

    // function to see all open processes with the .app suffix
    private void killBlacklistedApplications() {
        // clear the hashset of all open apps to get a fresh batch each timeline execution
        appsByGrouping.clear();
        appsToKill.clear();

        // go through all processes (active)
        ProcessHandle.allProcesses().forEach(process -> {
            if(!findLongRunningProcesses(process)) {
                return;
            }

            process.info().command().ifPresent(cmd -> {
                // if it contains .app, get to the parent path that ends w .app and put in the list
                if (cmd.contains(".app")) {
                    File file = findAppBundle(new File(cmd));
                    if (file != null) {
                        appsByGrouping.computeIfAbsent(file, _ -> new ArrayList<>()).add(process);
                    }
                }
            });
        });

        
        for(File app : appsByGrouping.keySet()) {
            if (blacklistedApps.contains(app)) {
                appsToKill.add(appsByGrouping.get(app)); // add it to 2d grouping list
            }
        }

        for(ArrayList<ProcessHandle> processGroup : appsToKill) {
            for(ProcessHandle processToKill : processGroup) {
                processToKill.destroyForcibly(); // kill all within a group BEFORE incrementing
            }
            destroyedAppCount.incrementAndGet();
            System.out.println("Destroyed " + destroyedAppCount + " applications.");
        }
    }

    private boolean findLongRunningProcesses(ProcessHandle process) {
        Optional<Instant> startTime = process.info().startInstant();
        if (startTime.isPresent()) {
            Duration uptime = Duration.between(startTime.get(), Instant.now());
            if (uptime.getSeconds() > 7) { 
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private File findAppBundle(File file) {
        File current = file;
        File lastPath = null;
        while (current != null) {
            if (current.getName().endsWith(".app")) {
                lastPath = current;
            }
            current = current.getParentFile();
        }
        return lastPath;
    }

    public File addAppToBlacklist(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null) {
            blacklistedApps.add(selectedFile);
        }
        return selectedFile;
    }

    public ObservableList<File> getBlacklistedApps() {
        return blacklistedApps;
    }

    public Map<File, ArrayList<ProcessHandle>> getGroupedAppsMap() {
        return appsByGrouping;
    }

    public AtomicInteger getDestroyedAppCount() {
        return destroyedAppCount;
    }
}
package com.femzyk.clock.gui;

import com.femzyk.clock.Clock;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ClockFxApp - JavaFX front end of the FEMZYK Clock Application.
 *
 * CS 1103-01, Programming Assignment Unit 3 (bonus front end). It REUSES
 * the exact same Clock class as the console demonstration: a background
 * updater thread keeps the time refreshed, while the JavaFX Application
 * Thread (the UI thread) displays it every second. The window therefore
 * follows the same threaded design as the console version, simply with a
 * graphical display instead of console printing.
 */
public class ClockFxApp extends Application {

    private static final String APP_TITLE = "FEMZYK Clock Application - CS 1103-01 Unit 3";

    private final Clock clock = new Clock();

    private Label timeLabel;
    private Label dateLabel;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        stage.setTitle(APP_TITLE);

        Label title = new Label("FEMZYK Clock");
        title.getStyleClass().add("header-title");

        timeLabel = new Label();
        timeLabel.setId("timeLabel");
        timeLabel.getStyleClass().add("time-label");

        dateLabel = new Label();
        dateLabel.setId("dateLabel");
        dateLabel.getStyleClass().add("date-label");

        statusLabel = new Label("Background updater thread running - time refreshes automatically");
        statusLabel.getStyleClass().add("status-label");

        VBox root = new VBox(10, title, timeLabel, dateLabel, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 560, 300);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // Background updater thread (lower priority than the UI thread):
        // keeps the shared Clock state fresh and hands each new value to
        // the JavaFX Application Thread via Platform.runLater.
        Thread updater = new Thread(() -> {
            while (clock.isRunning()) {
                clock.updateTime();
                String[] parts = clock.getFormattedTime().split(" ");
                Platform.runLater(() -> {
                    timeLabel.setText(parts[0]);
                    dateLabel.setText(parts.length > 1 ? parts[1] : "");
                });
                try {
                    Thread.sleep(Clock.UPDATE_INTERVAL_MS * 2);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "TimeUpdater");
        updater.setDaemon(true); // never blocks the JavaFX application from exiting
        updater.setPriority(Thread.NORM_PRIORITY);
        updater.start();
    }

    @Override
    public void stop() {
        clock.stop(); // clean shutdown of the background thread
    }
}

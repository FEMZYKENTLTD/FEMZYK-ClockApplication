package com.femzyk.clock;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clock - displays the current time and date continuously using threads.
 *
 * CS 1103-01, Programming Assignment Unit 3. The class keeps the current
 * time in a volatile field that a background thread refreshes, while a
 * separate, higher-priority display thread continuously prints it in the
 * readable format "HH:mm:ss dd-MM-yyyy". All shared state is volatile, so
 * the two threads never see an inconsistent value (concurrency safety),
 * and the running flag allows both threads to stop cleanly.
 */
public class Clock {

    /** Readable output format required by the assignment. */
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

    /** How often the background thread refreshes the time (milliseconds). */
    public static final long UPDATE_INTERVAL_MS = 100;

    /** How often the display thread prints the time (milliseconds). */
    public static final long DISPLAY_INTERVAL_MS = 1000;

    /** The most recent time, kept volatile so both threads always see the latest value. */
    private volatile LocalDateTime currentTime = LocalDateTime.now();

    /** Shared stop flag; volatile so the stop request is visible to both loops. */
    private volatile boolean running = true;

    /**
     * Refreshes the stored time with the current system time.
     * Called continuously by the background updating thread.
     */
    public void updateTime() {
        currentTime = LocalDateTime.now();
    }

    /**
     * Returns the current time in the "HH:mm:ss dd-MM-yyyy" format,
     * with error handling in case formatting ever fails.
     */
    public String getFormattedTime() {
        try {
            return currentTime.format(DISPLAY_FORMAT);
        } catch (DateTimeException error) {
            return "time formatting error: " + error.getMessage();
        }
    }

    /**
     * Background updating loop (runs on the lower-priority thread):
     * continuously refreshes the stored time and then sleeps briefly.
     * Exits cleanly when stop() is called or the thread is interrupted.
     */
    public void runTimeUpdateLoop() {
        while (running) {
            updateTime();
            try {
                Thread.sleep(UPDATE_INTERVAL_MS);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt(); // restore the interrupt status
                break;
            }
        }
    }

    /**
     * Display loop (runs on the higher-priority thread): the method that
     * continuously updates and prints the current time and date, as
     * required by the assignment. Exits cleanly when stop() is called.
     */
    public void runClockDisplayLoop() {
        while (running) {
            System.out.println("  " + getFormattedTime());
            System.out.flush();
            try {
                Thread.sleep(DISPLAY_INTERVAL_MS);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt(); // restore the interrupt status
                break;
            }
        }
    }

    /** Requests a clean shutdown of both clock threads. */
    public void stop() {
        running = false;
    }

    /** Returns true while the clock is running. */
    public boolean isRunning() {
        return running;
    }
}

import com.femzyk.clock.Clock;

/**
 * Main - demonstration program for the FEMZYK Clock Application.
 *
 * CS 1103-01, Programming Assignment Unit 3. Creates the Clock and the two
 * threads required by the assignment:
 *
 *   - "ClockDisplay": HIGHER priority (MAX_PRIORITY, 10) - runs the Clock
 *     method that continuously updates and prints the time.
 *   - "TimeUpdater": LOWER priority (NORM_PRIORITY, 5) - keeps the stored
 *     time refreshed in the background for better timekeeping precision.
 *
 * After a short demonstration run both threads are stopped cleanly with
 * the Clock's stop flag, and the program reports the shutdown.
 */
public class Main {

    /** How long the clock runs before the clean shutdown (milliseconds). */
    private static final int RUN_DURATION_MS = 15000;

    /**
     * Program entry point: starts the clock threads with different
     * priorities and stops them cleanly after the demonstration.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("        FEMZYK CLOCK APPLICATION (threads)    ");
        System.out.println("==============================================");

        Clock clock = new Clock();

        Thread displayThread = new Thread(clock::runClockDisplayLoop, "ClockDisplay");
        Thread updaterThread = new Thread(clock::runTimeUpdateLoop, "TimeUpdater");

        // Requirement: the display thread has a HIGHER priority than the
        // background updating thread, for better timekeeping precision.
        displayThread.setPriority(Thread.MAX_PRIORITY); // 10
        updaterThread.setPriority(Thread.NORM_PRIORITY); // 5

        System.out.println("ClockDisplay thread : priority " + displayThread.getPriority()
                + " (MAX)  - continuously updates and prints the time");
        System.out.println("TimeUpdater thread  : priority " + updaterThread.getPriority()
                + " (NORM) - refreshes the time in the background");
        System.out.println("----------------------------------------------");

        // Start both threads; the clock now runs concurrently.
        updaterThread.start();
        displayThread.start();

        // Let the clock run for the demonstration period, then stop it.
        try {
            Thread.sleep(RUN_DURATION_MS);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        } finally {
            clock.stop();
            try {
                displayThread.join(1000);
                updaterThread.join(1000);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
            }
            System.out.println("----------------------------------------------");
            System.out.println("Clock stopped cleanly after " + (RUN_DURATION_MS / 1000)
                    + " seconds. Both threads exited without conflicts.");
        }
    }
}

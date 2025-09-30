package se233.chapter6;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import java.util.concurrent.CountDownLatch;

@Suite
@SelectClasses({FoodTest.class, GameLoopTest.class, SnakeTest.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JUnitTestSuite {

    @BeforeAll
    public static void initJfxRuntime() {
        try {
            // Check if JavaFX Platform is already running
            if (!Platform.isFxApplicationThread()) {
                // Initialize JavaFX toolkit
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(() -> {
                    latch.countDown();
                });

                // Wait for JavaFX to be initialized
                try {
                    latch.await();
                    Thread.sleep(100); // Give it a moment to fully initialize
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IllegalStateException e) {
            // Platform might already be initialized, which is fine
            System.out.println("JavaFX Platform already initialized or initialization failed: " + e.getMessage());
        }
    }
}
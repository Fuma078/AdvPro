package se233.chapter2.controller;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import se233.chapter2.Launcher;
public class RefreshTask extends Task<Void> {
    @Override
    protected Void call() throws InterruptedException {
        for(;;) {
            try {
                Thread.sleep((long)(60 * 1e3));
            } catch (InterruptedException e) {
                System.out.println("Encountered an interrupted exception");
                break;
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Launcher.refreshPane();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            FutureTask<Void> futureTask = new FutureTask<>(new WatchTask());
            Platform.runLater(futureTask);

            try {
                futureTask.get();
            } catch (InterruptedException e) {
                System.out.println("Encountered an interrupted exception");
                break;
            } catch (ExecutionException e) {
                System.out.println("Encountered an execution exception");
            }
        }
        return null;
    }
}

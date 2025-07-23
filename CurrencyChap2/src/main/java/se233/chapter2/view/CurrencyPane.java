package se233.chapter2.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter2.controller.AllEventHandler;
import se233.chapter2.controller.draw.DrawGraphTask;
import se233.chapter2.controller.draw.DrawCurrencyInfoTask;
import se233.chapter2.controller.draw.DrawTopAreaTask;
import se233.chapter2.model.Currency;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CurrencyPane extends BorderPane {
    private Currency currency;
    private Button watch;
    private Button unwatch;
    private Button delete;

    public CurrencyPane(Currency currency) {
        this.currency = currency;
        this.watch = new Button("Watch");
        this.unwatch = new Button("Unwatch");
        this.delete = new Button("Delete");

        this.watch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onWatch(currency.getShortCode());
            }
        });

        this.unwatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onUnwatch(currency.getShortCode());
            }
        });

        this.delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onDelete(currency.getShortCode());
            }
        });

        this.setPadding(new Insets(0));
        this.setPrefSize(740, 450);
        this.setStyle("-fx-border-color: black");

        try {
            this.refreshPane(currency);
        } catch (ExecutionException e) {
            System.out.println("Encountered an execution exception.");
        } catch (InterruptedException e) {
            System.out.println("Encountered an interrupted exception.");
        }
    }

    public void refreshPane(Currency currency) throws ExecutionException, InterruptedException {
        this.currency = currency;

        // Create ExecutorService for managing concurrent tasks
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // Create FutureTasks for all three components
            FutureTask<Pane> currencyInfoTask = new FutureTask<>(new DrawCurrencyInfoTask(currency));
            FutureTask<VBox> currencyGraphTask = new FutureTask<>(new DrawGraphTask(currency));
            FutureTask<Pane> topAreaTask = new FutureTask<>(new DrawTopAreaTask(currency, watch, unwatch, delete));

            // Execute all tasks concurrently
            executor.execute(currencyInfoTask);
            executor.execute(currencyGraphTask);
            executor.execute(topAreaTask);

            // Get results from all tasks
            Pane currencyInfo = currencyInfoTask.get();
            VBox currencyGraph = currencyGraphTask.get();
            Pane topArea = topAreaTask.get();

            // Set the components to the BorderPane
            this.setTop(topArea);
            this.setLeft(currencyInfo);
            this.setCenter(currencyGraph);

        } finally {
            // Always shutdown the executor to free resources
            executor.shutdown();
        }
    }
}
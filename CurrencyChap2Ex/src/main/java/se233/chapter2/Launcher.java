package se233.chapter2;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import se233.chapter2.controller.Initialize;
import se233.chapter2.controller.RefreshTask;
import se233.chapter2.model.Currency;
import se233.chapter2.view.CurrencyParentPane;
import se233.chapter2.view.TopPane;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Launcher extends Application {
    private static Stage primaryStage;
    private static FlowPane mainPane;
    private static TopPane topPane;
    private static CurrencyParentPane currencyParentPane;
    private static List<Currency> currencyList;

    @Override
    public void start(Stage stage) throws ExecutionException, InterruptedException {
        primaryStage = stage;
        primaryStage.setTitle("Currency Watcher");
        primaryStage.setResizable(false);

        currencyList = Initialize.initializeApp();
        initMainPane();

        Scene mainScene = new Scene(mainPane);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        RefreshTask r = new RefreshTask();
        Thread th = new Thread(r);
        th.setDaemon(true);
        th.start();
    }

    public void initMainPane() throws ExecutionException, InterruptedException {
        mainPane = new FlowPane();
        mainPane.setStyle("-fx-background-color: #f0f0f0;"); // Add background
        topPane = new TopPane();
        currencyParentPane = new CurrencyParentPane(currencyList);

        mainPane.getChildren().add(topPane);
        mainPane.getChildren().add(currencyParentPane);
    }

    public static void refreshPane() throws InterruptedException, ExecutionException {
        topPane.refreshPane();
        currencyParentPane.refreshPane(currencyList);
        primaryStage.sizeToScene();
    }

    // Getter methods
    public static List<Currency> getCurrencyList() {
        return currencyList;
    }

    public static void setCurrencyList(List<Currency> currencyList) {
        Launcher.currencyList = currencyList;
    }

    // Added getter method for primaryStage
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // Additional getter methods for accessing other components if needed
    public static FlowPane getMainPane() {
        return mainPane;
    }

    public static TopPane getTopPane() {
        return topPane;
    }

    public static CurrencyParentPane getCurrencyParentPane() {
        return currencyParentPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package se233.chapter2.view;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter2.controller.AllEventHandler;
import se233.chapter2.controller.draw.DrawGraphTask;
import se233.chapter2.model.Currency;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
public class CurrencyPane extends BorderPane {
    private Currency currency;
    private Button watch;
    private Button delete;

    public CurrencyPane(Currency currency) {
        this.currency = currency;
        this.watch = new Button("Watch");
        this.delete = new Button("Delete");

        this.watch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onWatch(currency.getShortCode());
            }
        });

        this.delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onDelete(currency.getShortCode());
            }
        });

        this.setPadding(new Insets(0));
        this.setPrefSize(640, 300);
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
        Pane currencyInfo = genInfoPane();

        FutureTask<VBox> futureTask = new FutureTask<>(new DrawGraphTask(currency));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(futureTask);
        VBox currencyGraph = futureTask.get();
        executor.shutdown();

        Pane topArea = genTopArea();
        this.setTop(topArea);
        this.setLeft(currencyInfo);
        this.setCenter(currencyGraph);
    }

    private Pane genInfoPane() {
        VBox currencyInfoPane = new VBox(10);
        currencyInfoPane.setPadding(new Insets(5, 25, 5, 25));
        currencyInfoPane.setAlignment(Pos.CENTER);
        currencyInfoPane.setPrefWidth(170);

        Label exchangeString = new Label("");
        Label watchString = new Label("");
        exchangeString.setStyle("-fx-font-size: 20;");
        watchString.setStyle("-fx-font-size: 14;");

        if (this.currency != null && this.currency.getCurrent() != null) {

            double rate = this.currency.getCurrent().getRate();
            exchangeString.setText(String.format("%s: %.4f",
                    this.currency.getShortCode(), rate));

            if (this.currency.getWatch() == true && this.currency.getWatchRate() != null) {
                watchString.setText(String.format("(Watch @%.4f)", this.currency.getWatchRate()));
            }
        }

        currencyInfoPane.getChildren().addAll(exchangeString, watchString);
        return currencyInfoPane;
    }

    private HBox genTopArea() {
        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(5));
        topArea.getChildren().addAll(watch, delete);
        topArea.setAlignment(Pos.CENTER_RIGHT);
        return topArea;
    }
}
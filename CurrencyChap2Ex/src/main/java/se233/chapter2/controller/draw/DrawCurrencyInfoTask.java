package se233.chapter2.controller.draw;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter2.model.Currency;
import java.util.concurrent.Callable;
//
public class DrawCurrencyInfoTask implements Callable<Pane> {
    private Currency currency;

    public DrawCurrencyInfoTask(Currency currency) {
        this.currency = currency;
    }

    @Override
    public Pane call() throws Exception {
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
}
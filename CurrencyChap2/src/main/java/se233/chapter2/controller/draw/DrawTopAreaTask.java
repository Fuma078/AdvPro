package se233.chapter2.controller.draw;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import se233.chapter2.controller.AllEventHandler;
import se233.chapter2.model.Currency;
import java.util.concurrent.Callable;

public class DrawTopAreaTask implements Callable<Pane> {
    private Currency currency;
    private Button watch;
    private Button unwatch;
    private Button delete;

    public DrawTopAreaTask(Currency currency, Button watch, Button unwatch, Button delete) {
        this.currency = currency;
        this.watch = watch;
        this.unwatch = unwatch;
        this.delete = delete;
    }

    @Override
    public Pane call() throws Exception {
        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(5));
        topArea.getChildren().addAll(watch, unwatch, delete);
        topArea.setAlignment(Pos.CENTER_RIGHT);
        return topArea;
    }
}
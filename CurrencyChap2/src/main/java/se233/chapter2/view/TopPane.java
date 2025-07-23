package se233.chapter2.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import se233.chapter2.controller.AllEventHandler;
import se233.chapter2.controller.BaseCurrencyManager;
import se233.chapter2.Launcher;

import java.time.LocalDateTime;

public class TopPane extends FlowPane {
    private Button refresh;
    private Button add;
    private Button configureBaseCurrency;
    private Label update;
    private Label baseCurrencyLabel;

    public TopPane() {
        this.setPadding(new Insets(10));
        this.setHgap(10);
        this.setPrefSize(640, 20);

        // Initialize buttons
        refresh = new Button("Refresh");
        add = new Button("Add");
        configureBaseCurrency = new Button("Configure Base Currency");

        // Initialize labels
        update = new Label();
        baseCurrencyLabel = new Label("Base: " + BaseCurrencyManager.getBaseCurrency());

        // Set event handlers matching your existing pattern
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onRefresh();
            }
        });

        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onAdd();
            }
        });

        configureBaseCurrency.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get the primary stage from Launcher (you may need to add a getter method)
                AllEventHandler.onConfigureBaseCurrency(Launcher.getPrimaryStage());
            }
        });

        // Create a spacer to push base currency label to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Initialize the update label
        refreshPane();

        // Add all components to the pane
        this.getChildren().addAll(refresh, add, configureBaseCurrency, spacer, baseCurrencyLabel, update);
    }

    public void refreshPane() {
        update.setText(String.format("Last update: %s", LocalDateTime.now().toString()));
        // Update base currency label in case it changed
        baseCurrencyLabel.setText("Base: " + BaseCurrencyManager.getBaseCurrency());
    }

    // Getter methods for accessing the components
    public Button getRefresh() {
        return refresh;
    }

    public Button getAdd() {
        return add;
    }

    public Button getConfigureBaseCurrency() {
        return configureBaseCurrency;
    }

    public Label getUpdate() {
        return update;
    }

    public Label getBaseCurrencyLabel() {
        return baseCurrencyLabel;
    }
}
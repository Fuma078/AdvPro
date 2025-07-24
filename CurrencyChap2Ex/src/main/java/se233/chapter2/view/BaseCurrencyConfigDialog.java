package se233.chapter2.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se233.chapter2.controller.FetchData;
import java.util.*;

// 2.6.5

public class BaseCurrencyConfigDialog {
    private Stage parentStage;
    private String selectedBaseCurrency;
    private boolean isConfirmed = false;

    public BaseCurrencyConfigDialog (Stage parentStage) {
        this.parentStage = parentStage;
    }

    /**
     * Shows the base currency configuration dialog
     * @param currentBaseCurrency The currently selected base currency
     * @return Optional containing the selected base currency, or empty if cancelled
     */

    public Optional<String> showDialog(String currentBaseCurrency) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Configure Base Currency");
        dialog.setResizable(false);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        // Title label
        Label titleLabel = new Label("Select Base Currency");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Description label
        Label descLabel = new Label("Choose the base currency for exchange rate calculations:");
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        descLabel.setWrapText(true);

        // Currency selection area
        VBox currencySelectionBox = new VBox(10);
        currencySelectionBox.setAlignment(Pos.CENTER_LEFT);

        // ComboBox for common currencies
        Label comboLabel = new Label("Common Currencies:");
        ComboBox<String> currencyComboBox = new ComboBox<>();

        // Load common base currencies
        ObservableList<String> commonCurrencies = FXCollections.observableArrayList();
        try {
            List<String> availableCurrencies = FetchData.getCommonBaseCurrencies();
            commonCurrencies.addAll(availableCurrencies);
        } catch (Exception e) {
            // Fallback to basic list if fetching fails
            commonCurrencies.addAll("THB", "USD", "EUR", "GBP", "JPY", "CNY", "AUD", "CAD", "CHF", "SGD");
        }

        currencyComboBox.setItems(commonCurrencies);
        currencyComboBox.setValue(currentBaseCurrency != null ? currentBaseCurrency : "THB");
        currencyComboBox.setPrefWidth(200);

        // Custom currency input
        Label customLabel = new Label("Or enter custom currency code:");
        TextField customCurrencyField = new TextField();
        customCurrencyField.setPromptText("e.g., NOK, SEK, KRW");
        customCurrencyField.setPrefWidth(200);
        customCurrencyField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                currencyComboBox.getSelectionModel().clearSelection();
            }
        });

        // Clear custom field when combo box is selected
        currencyComboBox.setOnAction(e -> {
            if (currencyComboBox.getValue() != null) {
                customCurrencyField.clear();
            }
        });

        currencySelectionBox.getChildren().addAll(
                comboLabel, currencyComboBox,
                new Separator(),
                customLabel, customCurrencyField
        );

        // Validation label
        Label validationLabel = new Label("");
        validationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        // Button area
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button testButton = new Button("Test Currency");
        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        testButton.setOnAction(e -> {
            String testCurrency = getSelectedCurrency(currencyComboBox, customCurrencyField);
            if (testCurrency == null || testCurrency.trim().isEmpty()) {
                validationLabel.setText("Please select or enter a currency code.");
                return;
            }

            testButton.setDisable(true);
            testButton.setText("Testing...");
            validationLabel.setText("Testing currency validity...");
            validationLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 11px;");

            // Test in background thread to avoid blocking UI
            Thread testThread = new Thread(() -> {
                boolean isValid = FetchData.isValidBaseCurrency(testCurrency.trim().toUpperCase());

                // Update UI on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    testButton.setDisable(false);
                    testButton.setText("Test Currency");

                    if (isValid) {
                        validationLabel.setText("✓ Currency is valid and supported");
                        validationLabel.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
                        confirmButton.setDisable(false);
                    } else {
                        validationLabel.setText("✗ Currency is not supported as base currency");
                        validationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
                        confirmButton.setDisable(true);
                    }
                });
            });
            testThread.setDaemon(true);
            testThread.start();
        });

        confirmButton.setOnAction(e -> {
            String currency = getSelectedCurrency(currencyComboBox, customCurrencyField);
            if (currency != null && !currency.trim().isEmpty()) {
                selectedBaseCurrency = currency.trim().toUpperCase();
                isConfirmed = true;
                dialog.close();
            } else {
                validationLabel.setText("Please select or enter a currency code.");
                validationLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
            }
        });

        cancelButton.setOnAction(e -> {
            isConfirmed = false;
            dialog.close();
        });

        buttonBox.getChildren().addAll(testButton, confirmButton, cancelButton);

        // Add all components to main layout
        mainLayout.getChildren().addAll(
                titleLabel,
                descLabel,
                currencySelectionBox,
                validationLabel,
                buttonBox
        );

        Scene scene = new Scene(mainLayout, 350, 400);
        dialog.setScene(scene);

        // Show dialog and wait for user action
        dialog.showAndWait();

        return isConfirmed ? Optional.of(selectedBaseCurrency) : Optional.empty();
    }

    private String getSelectedCurrency(ComboBox<String> comboBox, TextField textField) {
        String customText = textField.getText().trim();
        if (!customText.isEmpty()) {
            return customText;
        }
        return comboBox.getValue();
    }
}
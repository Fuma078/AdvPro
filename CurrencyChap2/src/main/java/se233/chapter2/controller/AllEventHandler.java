package se233.chapter2.controller;

import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;
import se233.chapter2.controller.FetchData.InvalidCurrencyException;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllEventHandler {

    // Helper method to show error dialogs
    private static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper method to show information dialogs
    private static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void onRefresh() {
        try{
            Launcher.refreshPane();
        } catch (Exception e){
            e.printStackTrace();
            showErrorDialog("Refresh Error", "An error occurred while refreshing the application.");
        }
    }

    public static void onAdd() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Currency");
        dialog.setContentText("Currency code:");
        dialog.setHeaderText("Enter a valid 3-letter currency code");
        dialog.setGraphic(null);

        Optional<String> code = dialog.showAndWait();
        if (code.isPresent()){
            // Convert input to uppercase for case-insensitive handling
            String normalizedCode = code.get().trim().toUpperCase();

            // Validate input format
            if (normalizedCode.isEmpty()) {
                showErrorDialog("Invalid Input", "Currency code cannot be empty.");
                return;
            }

            // Basic format validation - currency codes should be 3 letters
            if (!normalizedCode.matches("^[A-Z]{3}$")) {
                showErrorDialog("Invalid Format",
                        "Currency code must be exactly 3 letters (e.g., USD, EUR, JPY).\n" +
                                "You entered: " + normalizedCode);
                return;
            }

            List<Currency> currencyList = Launcher.getCurrencyList();

            // Check if currency already exists (case-insensitive)
            boolean alreadyExists = false;
            for (Currency existingCurrency : currencyList) {
                if (existingCurrency.getShortCode().equalsIgnoreCase(normalizedCode)) {
                    alreadyExists = true;
                    break;
                }
            }

            if (alreadyExists) {
                showInfoDialog("Currency Already Added",
                        "Currency " + normalizedCode + " is already in your watchlist.");
                return;
            }

            // Try to fetch currency data with error handling using configured base currency
            try {
                Currency c = new Currency(normalizedCode);
                String baseCurrency = BaseCurrencyManager.getBaseCurrency();
                // Changed from 8 to 30 days for historical data
                List<CurrencyEntity> cList = FetchData.fetchRange(c.getShortCode(), 30, baseCurrency);

                // Validate that we got data
                if (cList == null || cList.isEmpty()) {
                    showErrorDialog("No Data Available",
                            "No exchange rate data available for currency: " + normalizedCode);
                    return;
                }

                c.setHistorical(cList);
                c.setCurrent(cList.get(cList.size()-1));
                currencyList.add(c);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();

                // Show success message
                showInfoDialog("Currency Added",
                        "Successfully added " + normalizedCode + " to your watchlist!");

            } catch (InvalidCurrencyException e) {
                // Handle our custom exception for invalid currency codes
                showErrorDialog("Invalid Currency Code", e.getMessage());
            } catch (Exception e) {
                // Handle any other unexpected errors
                showErrorDialog("Error Adding Currency",
                        "An unexpected error occurred while adding " + normalizedCode + ":\n" +
                                e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void onDelete(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            int index =-1;
            for(int i=0 ; i<currencyList.size() ; i++) {
                // Use case-insensitive comparison for deletion
                if (currencyList.get(i).getShortCode().equalsIgnoreCase(code)) {
                    index = i;
                    break;
                }
            }
            if (index !=-1) {
                currencyList.remove(index);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
                showInfoDialog("Currency Removed",
                        "Successfully removed " + code + " from your watchlist.");
            } else {
                showErrorDialog("Currency Not Found",
                        "Currency " + code + " was not found in your watchlist.");
            }
        } catch (InterruptedException e) {
            showErrorDialog("Operation Interrupted", "The delete operation was interrupted.");
            e.printStackTrace();
        } catch (ExecutionException e) {
            showErrorDialog("Execution Error", "An error occurred during the delete operation.");
            e.printStackTrace();
        }
    }

    public static void onWatch(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            int index =-1;
            for(int i = 0 ; i < currencyList.size() ; i++) {
                // Use case-insensitive comparison for watch
                if (currencyList.get(i).getShortCode().equalsIgnoreCase(code)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Watch");
                dialog.setContentText("Enter watch rate:");
                dialog.setHeaderText("Set an alert rate for " + code);
                dialog.setGraphic(null);

                Optional<String> retrievedRate = dialog.showAndWait();
                if (retrievedRate.isPresent()){
                    try {
                        String rateString = retrievedRate.get().trim();
                        if (rateString.isEmpty()) {
                            showErrorDialog("Invalid Input", "Watch rate cannot be empty.");
                            return;
                        }

                        double rate = Double.parseDouble(rateString);

                        if (rate <= 0) {
                            showErrorDialog("Invalid Rate", "Watch rate must be a positive number.");
                            return;
                        }

                        currencyList.get(index).setWatch(true);
                        currencyList.get(index).setWatchRate(rate);
                        Launcher.setCurrencyList(currencyList);
                        Launcher.refreshPane();

                        showInfoDialog("Watch Added",
                                "Successfully set watch alert for " + code + " at rate: " + rate);

                    } catch (NumberFormatException e) {
                        showErrorDialog("Invalid Number Format",
                                "Please enter a valid decimal number for the watch rate.");
                    }
                }
            } else {
                showErrorDialog("Currency Not Found",
                        "Currency " + code + " was not found in your watchlist.");
            }
        } catch (InterruptedException e) {
            showErrorDialog("Operation Interrupted", "The watch operation was interrupted.");
            e.printStackTrace();
        } catch (ExecutionException e) {
            showErrorDialog("Execution Error", "An error occurred during the watch operation.");
            e.printStackTrace();
        }
    }

    public static void onUnwatch(String code) {
        try {
            List<Currency> currencyList = Launcher.getCurrencyList();
            int index = -1;
            for(int i = 0; i < currencyList.size(); i++) {
                // Use case-insensitive comparison for unwatch
                if (currencyList.get(i).getShortCode().equalsIgnoreCase(code)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                // Check if watch was actually active
                boolean wasWatching = currencyList.get(index).getWatch();

                // Remove watch monitoring by setting watch to false and resetting watch rate
                currencyList.get(index).setWatch(false);
                currencyList.get(index).setWatchRate(0.0);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();

                if (wasWatching) {
                    showInfoDialog("Watch Removed",
                            "Successfully removed watch alert for " + code + ".");
                } else {
                    showInfoDialog("No Watch Active",
                            code + " was not being watched.");
                }
            } else {
                showErrorDialog("Currency Not Found",
                        "Currency " + code + " was not found in your watchlist.");
            }
        } catch (InterruptedException e) {
            showErrorDialog("Operation Interrupted", "The unwatch operation was interrupted.");
            e.printStackTrace();
        } catch (ExecutionException e) {
            showErrorDialog("Execution Error", "An error occurred during the unwatch operation.");
            e.printStackTrace();
        }
    }

    /**
     * Opens the base currency configuration dialog
     * @param parentStage The parent stage for the dialog
     */
    public static void onConfigureBaseCurrency(Stage parentStage) {
        try {
            BaseCurrencyManager.showConfigurationDialog(parentStage);
        } catch (Exception e) {
            showErrorDialog("Configuration Error",
                    "An error occurred while opening the base currency configuration:\n" +
                            e.getMessage());
            e.printStackTrace();
        }
    }
}
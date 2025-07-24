package se233.chapter2.controller;

import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;
import se233.chapter2.controller.FetchData.InvalidCurrencyException;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.ArrayList;

public class Initialize {

    // Helper method to show error dialogs
    private static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static List<Currency> initializeApp() {
        List<Currency> currencyList = new ArrayList<>();
        String baseCurrency = BaseCurrencyManager.getBaseCurrency();

        try {
            // Don't initialize with the same currency as the base currency
            // If base is THB, initialize with USD; if base is USD, initialize with THB
            String initCurrency = baseCurrency.equals("THB") ? "USD" : "THB";

            Currency c = new Currency(initCurrency);
            List<CurrencyEntity> cList = FetchData.fetchRange(c.getShortCode(), 30, baseCurrency);

            if (cList != null && !cList.isEmpty()) {
                c.setHistorical(cList);
                c.setCurrent(cList.get(cList.size() - 1));
                currencyList.add(c);
            } else {
                // Try EUR as second fallback
                System.err.println("Failed to initialize with " + initCurrency + ", trying EUR as fallback");
                try {
                    Currency fallbackCurrency = new Currency("EUR");
                    List<CurrencyEntity> fallbackList = FetchData.fetchRange(fallbackCurrency.getShortCode(), 30, baseCurrency);

                    if (fallbackList != null && !fallbackList.isEmpty()) {
                        fallbackCurrency.setHistorical(fallbackList);
                        fallbackCurrency.setCurrent(fallbackList.get(fallbackList.size() - 1));
                        currencyList.add(fallbackCurrency);
                    }
                } catch (InvalidCurrencyException e) {
                    System.err.println("Fallback currency EUR also failed: " + e.getMessage());
                }
            }

        } catch (InvalidCurrencyException e) {
            System.err.println("Failed to initialize default currency: " + e.getMessage());
            showErrorDialog("Initialization Error",
                    "Failed to load default currency data:\n" + e.getMessage() +
                            "\n\nThe application will start with an empty currency list." +
                            "\nBase currency: " + baseCurrency);
        } catch (Exception e) {
            System.err.println("Unexpected error during initialization: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Initialization Error",
                    "An unexpected error occurred during initialization:\n" + e.getMessage() +
                            "\n\nThe application will start with an empty currency list." +
                            "\nBase currency: " + baseCurrency);
        }

        // Always return a list, even if empty
        return currencyList;
    }
}
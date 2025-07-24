package se233.chapter2.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import se233.chapter2.view.BaseCurrencyConfigDialog;
import se233.chapter2.Launcher;
import java.io.*;
import java.util.Properties;
import java.util.Optional;

// 2.6.5

/**
 * Manages the application's base currency configuration
 * Stores and retrieves the base currency setting from a properties file
 */

public class BaseCurrencyManager {
    private static final String CONFIG_FILE = "currency_config.properties";
    private static final String BASE_CURRENCY_KEY = "base.currency";
    private static final String DEFAULT_BASE_CURRENCY = "THB"; // THB as base to show USD = ~32

    private static String currentBaseCurrency = null;

    /**
     * Gets the current base currency
     * @return The current base currency code
     */

    public static String getBaseCurrency() {
        if (currentBaseCurrency == null) {
            loadBaseCurrency();
        }
        return currentBaseCurrency;
    }

    /**
     * Sets and saves the base currency
     * @param baseCurrency The new base currency code
     * @return true if successfully saved, false otherwise
     */

    public static boolean setBaseCurrency(String baseCurrency) {
        if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
            return false;
        }

        String normalizedCurrency = baseCurrency.trim().toUpperCase();

        // Validate currency format
        if (!normalizedCurrency.matches("^[A-Z]{3}$")) {
            return false;
        }

        try {
            saveBaseCurrency(normalizedCurrency);
            currentBaseCurrency = normalizedCurrency;
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save base currency configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads the base currency from the configuration file
     */

    private static void loadBaseCurrency() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                String loadedCurrency = props.getProperty(BASE_CURRENCY_KEY, DEFAULT_BASE_CURRENCY);

                // Validate loaded currency
                if (loadedCurrency != null && loadedCurrency.matches("^[A-Z]{3}$")) {
                    currentBaseCurrency = loadedCurrency;
                } else {
                    currentBaseCurrency = DEFAULT_BASE_CURRENCY;
                }
            } catch (IOException e) {
                System.err.println("Failed to load base currency configuration: " + e.getMessage());
                currentBaseCurrency = DEFAULT_BASE_CURRENCY;
            }
        } else {
            currentBaseCurrency = DEFAULT_BASE_CURRENCY;
        }
    }

    /**
     * Saves the base currency to the configuration file
     * @param baseCurrency The base currency to save
     * @throws IOException If saving fails
     */

    private static void saveBaseCurrency(String baseCurrency) throws IOException {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        // Load existing properties if file exists
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }
        }

        // Set the base currency property
        props.setProperty(BASE_CURRENCY_KEY, baseCurrency);

        // Save properties to file
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "Currency Watcher Configuration");
        }
    }

    /**
     * Resets the base currency to default
     * @return true if successfully reset, false otherwise
     */

    public static boolean resetToDefault() {
        return setBaseCurrency(DEFAULT_BASE_CURRENCY);
    }

    /**
     * Gets the default base currency
     * @return The default base currency code
     */

    public static String getDefaultBaseCurrency() {
        return DEFAULT_BASE_CURRENCY;
    }

    /**
     * Shows the base currency configuration dialog
     * @param parentStage The parent stage for the dialog
     * @return true if base currency was changed, false otherwise
     */

    public static boolean showConfigurationDialog(Stage parentStage) {
        BaseCurrencyConfigDialog dialog = new BaseCurrencyConfigDialog(parentStage);
        Optional<String> result = dialog.showDialog(getBaseCurrency());

        if (result.isPresent()) {
            String newBaseCurrency = result.get();
            if (!newBaseCurrency.equals(getBaseCurrency())) {
                if (setBaseCurrency(newBaseCurrency)) {
                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Base Currency Changed");
                    alert.setHeaderText(null);
                    alert.setContentText("Base currency changed to " + newBaseCurrency +
                            ". The application will refresh to update all exchange rates.");
                    alert.showAndWait();

                    // Trigger application refresh
                    try {
                        Launcher.refreshPane();
                    } catch (Exception e) {
                        System.err.println("Error refreshing application after base currency change: " + e.getMessage());
                    }

                    return true;
                } else {
                    // Show error message
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Configuration Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to save the new base currency configuration.");
                    alert.showAndWait();
                }
            }
        }
        return false;
    }

    /**
     * Checks if the current base currency is the default
     * @return true if using default base currency, false otherwise
     */

    public static boolean isUsingDefaultBaseCurrency() {
        return DEFAULT_BASE_CURRENCY.equals(getBaseCurrency());
    }
}
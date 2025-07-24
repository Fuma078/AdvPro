package se233.chapter2.controller;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import se233.chapter2.model.CurrencyEntity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FetchData {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Custom exception for invalid currency codes
    public static class InvalidCurrencyException extends Exception {
        public InvalidCurrencyException(String message) {
            super(message);
        }
    }

    // Original method with THB as default base (for showing USD = ~32)
    public static List<CurrencyEntity> fetchRange(String symbol, int N) throws InvalidCurrencyException {
        return fetchRange(symbol, N, "THB");
    }

    // Enhanced method with custom base currency support
    public static List<CurrencyEntity> fetchRange(String symbol, int N, String baseCurrency) throws InvalidCurrencyException {
        String dateEnd = LocalDate.now().format(formatter);
        String dateStart = LocalDate.now().minusDays(N).format(formatter);

        // Validate inputs
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new InvalidCurrencyException("Symbol currency cannot be empty");
        }
        if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
            throw new InvalidCurrencyException("Base currency cannot be empty");
        }

        String normalizedSymbol = symbol.trim().toUpperCase();
        String normalizedBase = baseCurrency.trim().toUpperCase();

        // Validate currency code formats
        if (!normalizedSymbol.matches("^[A-Z]{3}$")) {
            throw new InvalidCurrencyException("Invalid symbol currency format: " + normalizedSymbol +
                    ". Must be 3 letters (e.g., USD, EUR, JPY)");
        }
        if (!normalizedBase.matches("^[A-Z]{3}$")) {
            throw new InvalidCurrencyException("Invalid base currency format: " + normalizedBase +
                    ". Must be 3 letters (e.g., USD, EUR, JPY)");
        }

        // Special case: if symbol and base are the same, return rate of 1.0 for all dates
        if (normalizedSymbol.equals(normalizedBase)) {
            List<CurrencyEntity> histList = new ArrayList<>();
            LocalDate currentDate = LocalDate.now();

            for (int i = 0; i <= N; i++) {
                LocalDate date = currentDate.minusDays(i);
                String dateString = date.format(formatter);
                histList.add(new CurrencyEntity(1.0, dateString));
            }

            // Sort by date (oldest first)
            histList.sort(new Comparator<CurrencyEntity>() {
                @Override
                public int compare(CurrencyEntity o1, CurrencyEntity o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });

            return histList;
        }

        String urlStr = String.format("https://cmu.to/SE233currencyapi?base=%s&symbol=%s&start_date=%s&end_date=%s",
                normalizedBase, normalizedSymbol, dateStart, dateEnd);

        List<CurrencyEntity> histList = new ArrayList<>();
        try {
            String retrievedJson = IOUtils.toString(new URL(urlStr), Charset.defaultCharset());
            JSONObject jsonResponse = new JSONObject(retrievedJson);

            // Check if the API returned an error
            if (jsonResponse.has("error")) {
                String errorMessage = jsonResponse.getString("error");
                if (errorMessage.toLowerCase().contains("base")) {
                    throw new InvalidCurrencyException("Invalid base currency: " + normalizedBase +
                            ". This currency is not supported as a base currency.");
                } else if (errorMessage.toLowerCase().contains("symbol")) {
                    throw new InvalidCurrencyException("Invalid symbol currency: " + normalizedSymbol +
                            ". This currency is not supported.");
                } else {
                    throw new InvalidCurrencyException("API Error: " + errorMessage);
                }
            }

            // Check if rates object exists
            if (!jsonResponse.has("rates")) {
                throw new InvalidCurrencyException("Invalid currency pair: " + normalizedSymbol + "/" + normalizedBase +
                        ". No rate data available.");
            }

            JSONObject jsonOBJ = jsonResponse.getJSONObject("rates");

            // Check if rates object is empty
            if (jsonOBJ.length() == 0) {
                throw new InvalidCurrencyException("No rate data available for " + normalizedSymbol +
                        " with base currency " + normalizedBase +
                        ". Please verify both currencies are valid and supported.");
            }

            Iterator<String> keysToCopyIterator = jsonOBJ.keys();

            while (keysToCopyIterator.hasNext()) {
                String key = keysToCopyIterator.next();
                try {
                    Double rate = Double.parseDouble(jsonOBJ.get(key).toString());
                    histList.add(new CurrencyEntity(rate, key));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid rate data for date: " + key + " (skipping this entry)");
                    // Continue processing other dates instead of failing completely
                }
            }

            // Check if we got any valid data
            if (histList.isEmpty()) {
                throw new InvalidCurrencyException("No valid rate data found for currency pair: " +
                        normalizedSymbol + "/" + normalizedBase);
            }

            histList.sort(new Comparator<CurrencyEntity>() {
                @Override
                public int compare(CurrencyEntity o1, CurrencyEntity o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });

        } catch (MalformedURLException e) {
            System.err.println("Encounter a Malformed Url exception: " + e.getMessage());
            throw new InvalidCurrencyException("Network error: Invalid URL format");
        } catch (IOException e) {
            System.err.println("Encounter an IO exception: " + e.getMessage());
            throw new InvalidCurrencyException("Network error: Unable to fetch currency data. Please check your internet connection.");
        } catch (Exception e) {
            if (e instanceof InvalidCurrencyException) {
                throw e; // Re-throw our custom exception
            }
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new InvalidCurrencyException("Unexpected error occurred while fetching currency data for " +
                    normalizedSymbol + "/" + normalizedBase);
        }

        return histList;
    }

    // Helper method to test if a currency is valid as a base currency
    public static boolean isValidBaseCurrency(String baseCurrency) {
        if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
            return false;
        }

        String normalizedBase = baseCurrency.trim().toUpperCase();

        // Validate format first
        if (!normalizedBase.matches("^[A-Z]{3}$")) {
            return false;
        }

        try {
            // Test with a different currency to avoid same-currency issues
            String testCurrency = normalizedBase.equals("USD") ? "EUR" : "USD";
            List<CurrencyEntity> testResult = fetchRange(testCurrency, 1, baseCurrency);
            return testResult != null && !testResult.isEmpty();
        } catch (InvalidCurrencyException e) {
            return false;
        }
    }

    // Helper method to get available base currencies (common ones that usually work)
    public static List<String> getCommonBaseCurrencies() {
        List<String> commonBases = new ArrayList<>();
        String[] currencies = {"THB", "USD", "EUR", "GBP", "JPY", "CNY", "AUD", "CAD", "CHF", "SGD"};

        for (String currency : currencies) {
            if (isValidBaseCurrency(currency)) {
                commonBases.add(currency);
            }
        }

        // Always include THB and USD as fallbacks even if validation fails
        if (!commonBases.contains("THB")) commonBases.add(0, "THB");
        if (!commonBases.contains("USD")) commonBases.add(1, "USD");

        return commonBases;
    }

}
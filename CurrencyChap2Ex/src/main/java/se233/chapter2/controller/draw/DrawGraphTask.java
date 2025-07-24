package se233.chapter2.controller.draw;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

public class DrawGraphTask implements Callable<VBox> {
    Currency currency;

    public DrawGraphTask(Currency currency) {
        this.currency = currency;
    }

    @Override
    public VBox call() throws Exception {
        VBox graphPane = new VBox(10);
        graphPane.setPadding(new Insets(0, 25, 25, 25));

        // Create categories list first
        ObservableList<String> categories = FXCollections.observableArrayList();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(true);
        lineChart.setPrefHeight(300);

        // Add explicit styling to ensure labels are visible
        lineChart.setStyle("-fx-font-size: 10px;");

        if (this.currency != null && this.currency.getHistorical() != null && !this.currency.getHistorical().isEmpty()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<CurrencyEntity> historicalData = currency.getHistorical();

            // Process all data first to get categories
            List<String> allFormattedDates = new ArrayList<>();

            for (CurrencyEntity c : historicalData) {
                String originalDate = c.getTimestamp();
                String formattedDate;

                try {
                    LocalDate date = LocalDate.parse(originalDate, inputFormatter);
                    formattedDate = date.format(outputFormatter);
                } catch (Exception e) {
                    // Fallback formatting
                    if (originalDate.length() >= 10) {
                        String month = originalDate.substring(5, 7);
                        String day = originalDate.substring(8, 10);
                        // Remove leading zeros
                        int monthInt = Integer.parseInt(month);
                        int dayInt = Integer.parseInt(day);
                        formattedDate = monthInt + "/" + dayInt;
                    } else {
                        formattedDate = originalDate;
                    }
                }

                allFormattedDates.add(formattedDate);

                if (c.getRate() > maxY) maxY = c.getRate();
                if (c.getRate() < minY) minY = c.getRate();
            }

            // Select which dates to show - show first, last, and evenly spaced ones
            int totalDates = allFormattedDates.size();
            int maxLabels = 8;

            for (int i = 0; i < totalDates; i++) {
                String dateLabel = allFormattedDates.get(i);

                // Always show first and last dates, plus evenly spaced ones
                boolean shouldShowLabel = (i == 0) ||
                        (i == totalDates - 1) ||
                        (i % Math.max(1, totalDates / (maxLabels - 2)) == 0);

                if (shouldShowLabel) {
                    categories.add(dateLabel);
                } else {
                    categories.add(dateLabel); // Still add to categories but will be filtered by axis
                }

                // Add data point
                series.getData().add(new XYChart.Data<>(dateLabel, historicalData.get(i).getRate()));
            }

            // Set categories on the axis BEFORE creating the line chart
            xAxis.setCategories(categories);

            // Configure axes
            xAxis.setAutoRanging(true);
            xAxis.setTickLabelRotation(45);
            xAxis.setTickMarkVisible(true);
            xAxis.setTickLabelsVisible(true);

            // Try to show fewer tick labels to prevent crowding
            if (categories.size() > 10) {
                xAxis.setTickLabelGap(2);
            }

            yAxis.setAutoRanging(false);
            double padding = (maxY - minY) * 0.1;
            yAxis.setLowerBound(Math.max(0, minY - padding));
            yAxis.setUpperBound(maxY + padding);
            yAxis.setTickUnit((maxY - minY) / 5);

            lineChart.getData().add(series);

            // Force a layout pass to ensure labels are rendered
            lineChart.autosize();
        }

        graphPane.getChildren().add(lineChart);
        return graphPane;
    }
}
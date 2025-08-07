package se233.chapter3.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.scene.layout.Region;
import se233.chapter3.Launcher;
import se233.chapter3.model.FileFreq;
import se233.chapter3.model.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MainViewController {
    LinkedHashMap<String, List<FileFreq>> uniqueSets;

    @FXML
    private ListView<String> inputListView;
    @FXML
    private Button startButton;
    @FXML
    private ListView<String> listView;

    @FXML
    public void handleCloseAction() {
        Platform.exit();
    }

    @FXML
    public void initialize() {
        inputListView.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".pdf");
            if (db.hasFiles() && isAccepted) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        inputListView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                for (File file : db.getFiles()) {
                    String filePath = file.getAbsolutePath();
                    // Display only filename in the ListView
                    String fileName = Paths.get(filePath).getFileName().toString();
                    inputListView.getItems().add(fileName);
                    // Store the full path separately for processing
                    inputListView.getProperties().put(fileName, filePath);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        startButton.setOnAction(event -> {
            Parent bgRoot = Launcher.primaryStage.getScene().getRoot();
            Task<Void> processTask = new Task<Void>() {
                @Override
                public Void call() throws IOException {
                    ProgressIndicator pi = new ProgressIndicator();
                    VBox box = new VBox(pi);
                    box.setAlignment(Pos.CENTER);
                    Launcher.primaryStage.getScene().setRoot(box);

                    ExecutorService executor = Executors.newFixedThreadPool(4);
                    final ExecutorCompletionService<Map<String, FileFreq>> completionService = new ExecutorCompletionService<>(executor);
                    List<String> inputListViewItems = inputListView.getItems();
                    int total_files = inputListViewItems.size();
                    Map<String, FileFreq>[] wordMap = new Map[total_files];

                    for (int i = 0; i < total_files; i++) {
                        try {
                            String fileName = inputListViewItems.get(i);
                            String filePath = (String) inputListView.getProperties().get(fileName);
                            PdfDocument p = new PdfDocument(filePath);
                            completionService.submit(new WordCountMapTask(p));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    for (int i = 0; i < total_files; i++) {
                        try {
                            Future<Map<String, FileFreq>> future = completionService.take();
                            wordMap[i] = future.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        WordCountReduceTask merger = new WordCountReduceTask(wordMap);
                        Future<LinkedHashMap<String, List<FileFreq>>> future = executor.submit(merger);
                        uniqueSets = future.get();
                        Platform.runLater(() -> {
                            // Format display for all words with their frequencies
                            for (Map.Entry<String, List<FileFreq>> entry : uniqueSets.entrySet()) {
                                String word = entry.getKey();
                                List<FileFreq> fileFreqs = entry.getValue();

                                // Show frequencies for all words (both single and multiple documents)
                                String frequencies = fileFreqs.stream()
                                        .map(ff -> String.valueOf(ff.getFreq()))
                                        .collect(Collectors.joining(", "));
                                listView.getItems().add(word + " (" + frequencies + ")");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        executor.shutdown();
                    }
                    return null;
                }
            };

            processTask.setOnSucceeded(e -> {
                Launcher.primaryStage.getScene().setRoot(bgRoot);
            });

            Thread thread = new Thread(processTask);
            thread.setDaemon(true);
            thread.start();
        });

        listView.setOnMouseClicked(event -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;

            // Extract word from display format (remove frequency info if present)
            String word = selectedItem.contains(" (") ?
                    selectedItem.substring(0, selectedItem.indexOf(" (")) : selectedItem;

            List<FileFreq> listOfLinks = uniqueSets.get(word);
            if (listOfLinks == null) return;

            ListView<FileFreq> popupListView = new ListView<>();
            LinkedHashMap<FileFreq, String> lookupTable = new LinkedHashMap<>();

            for (FileFreq fileFreq : listOfLinks) {
                lookupTable.put(fileFreq, fileFreq.getPath());
                popupListView.getItems().add(fileFreq);
            }

            popupListView.setPrefWidth(Region.USE_COMPUTED_SIZE);
            popupListView.setPrefHeight(popupListView.getItems().size() * 40);

            popupListView.setOnMouseClicked(innerEvent -> {
                FileFreq selectedFileFreq = popupListView.getSelectionModel().getSelectedItem();
                if (selectedFileFreq != null) {
                    Launcher.hs.showDocument("file:///" + lookupTable.get(selectedFileFreq));
                    popupListView.getScene().getWindow().hide();
                }
            });

            // Add ESC key handler to close popup
            popupListView.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    popupListView.getScene().getWindow().hide();
                }
            });

            Popup popup = new Popup();
            popup.getContent().add(popupListView);

            // Make popup focusable so it can receive key events
            popup.setAutoFix(true);
            popup.setAutoHide(true);

            popup.show(Launcher.primaryStage);

            // Request focus for ESC key handling
            popupListView.requestFocus();
        });
    }
}
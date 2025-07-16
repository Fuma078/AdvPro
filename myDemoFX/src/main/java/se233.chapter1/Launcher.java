package se233.chapter1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import se233.chapter1.controller.GenCharacter;
import se233.chapter1.controller.GenItemList;
import se233.chapter1.controller.AllCustomHandler;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;
import se233.chapter1.model.item.Armor;
import se233.chapter1.view.CharacterPane;
import se233.chapter1.view.EquipPane;
import se233.chapter1.view.InventoryPane;
import java.util.ArrayList;

public class Launcher extends Application {
    private static Scene mainScene;
    private static BasedCharacter mainCharacter = null;
    private static ArrayList<BasedEquipment> allEquipments = null;
    private static Weapon equippedWeapon = null;
    private static Armor equippedArmor = null;
    private static CharacterPane characterPane = null;
    private static EquipPane equipPane = null;
    private static InventoryPane inventoryPane = null;

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Starting JavaFX application...");
            primaryStage.setTitle("Chapter1");
            primaryStage.setResizable(false);

            // Initialize character and equipment
            mainCharacter = GenCharacter.setUpCharacter();
            allEquipments = GenItemList.setUpItemList();

            // Create main pane
            BorderPane mainPane = getMainPane();

            // Create and set scene
            mainScene = new Scene(mainPane, 500, 600);
            primaryStage.setScene(mainScene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public BorderPane getMainPane() {
        BorderPane mainPane = new BorderPane();

        characterPane = new CharacterPane();
        equipPane = new EquipPane();
        inventoryPane = new InventoryPane();

        refreshPane();

        // Set up drag event handlers for invalid drop zones
        setupInvalidDropZoneHandlers(mainPane);

        mainPane.setCenter(characterPane);
        mainPane.setLeft(equipPane);
        mainPane.setBottom(inventoryPane);

        return mainPane;
    }

    private void setupInvalidDropZoneHandlers(BorderPane mainPane) {
        // Set up drag over handler for the main pane (invalid drop zone)
        mainPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                AllCustomHandler.onInvalidDragOver(event);
            }
        });

        // Set up drag dropped handler for the main pane (invalid drop zone)
        mainPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                AllCustomHandler.onInvalidDragDropped(event);
            }
        });

        // Set up handlers for character pane (invalid drop zone)
        characterPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                AllCustomHandler.onInvalidDragOver(event);
            }
        });

        characterPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                AllCustomHandler.onInvalidDragDropped(event);
            }
        });

        // Set up handlers for inventory pane (invalid drop zone)
        inventoryPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                AllCustomHandler.onInvalidDragOver(event);
            }
        });

        inventoryPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                AllCustomHandler.onInvalidDragDropped(event);
            }
        });
    }

    public static void refreshPane() {
        if (characterPane != null) {
            characterPane.drawPane(mainCharacter);
        }
        if (equipPane != null) {
            equipPane.drawPane(equippedWeapon, equippedArmor);
        }
        if (inventoryPane != null) {
            inventoryPane.drawPane(allEquipments);
        }
    }

    public static BasedCharacter getMainCharacter() {
        return mainCharacter;
    }

    public static void setMainCharacter(BasedCharacter mainCharacter) {
        Launcher.mainCharacter = mainCharacter;
    }

    public static ArrayList<BasedEquipment> getAllEquipments() {
        return allEquipments;
    }

    public static void setAllEquipments(ArrayList<BasedEquipment> allEquipments) {
        Launcher.allEquipments = allEquipments;
    }

    public static Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public static void setEquippedWeapon(Weapon equippedWeapon) {
        Launcher.equippedWeapon = equippedWeapon;
    }

    public static Armor getEquippedArmor() {
        return equippedArmor;
    }

    public static void setEquippedArmor(Armor equippedArmor) {
        Launcher.equippedArmor = equippedArmor;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package se233.chapter1.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import se233.chapter1.Launcher;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;
import se233.chapter1.model.item.Armor;

public class AllCustomHandler {
    // Track the item that was replaced during swap
    private static BasedEquipment replacedItem = null;

    public static class GenCharacterHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            // Auto-unequip all items before generating new character
            unequipAllItems();

            // Generate new character
            Launcher.setMainCharacter(GenCharacter.setUpCharacter());
            Launcher.refreshPane();
        }

        /**
         * Automatically unequips all currently equipped items (removes them completely)
         */
        private static void unequipAllItems() {
            // Simply set equipped items to null without adding them back to inventory
            Launcher.setEquippedWeapon(null);
            Launcher.setEquippedArmor(null);
        }
    }

    public static void onDragDetected(MouseEvent event, BasedEquipment equipment, ImageView imgView) {
        Dragboard db = imgView.startDragAndDrop(TransferMode.ANY);
        db.setDragView(imgView.getImage());
        ClipboardContent content = new ClipboardContent();
        content.put(equipment.DATA_FORMAT, equipment);
        db.setContent(content);
        event.consume();
    }

    public static void onDragOver(DragEvent event, String type) {
        Dragboard dragboard = event.getDragboard();
        BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(
                BasedEquipment.DATA_FORMAT);

        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT) &&
                retrievedEquipment.getClass().getSimpleName().equals(type)) {

            // Check if the character can equip this item
            BasedCharacter character = Launcher.getMainCharacter();
            if (canEquipItem(character, retrievedEquipment)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        }
    }

    public static void onDragDropped(DragEvent event, Label lbl, StackPane imgGroup) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
        replacedItem = null; // Reset replaced item tracker

        if(dragboard.hasContent(BasedEquipment.DATA_FORMAT)) {
            BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(
                    BasedEquipment.DATA_FORMAT);
            BasedCharacter character = Launcher.getMainCharacter();

            // Check if equipment can be equipped based on character type and damage type
            if (!canEquipItem(character, retrievedEquipment)) {
                // Equipment cannot be equipped, show error message or handle gracefully
                System.out.println("Cannot equip " + retrievedEquipment.getName() +
                        " - incompatible with " + character.getName() +
                        " (Character type: " + character.getType() + ")");
                event.setDropCompleted(false);
                return;
            }

            if(retrievedEquipment.getClass().getSimpleName().equals("Weapon")) {
                if (Launcher.getEquippedWeapon() != null) {
                    replacedItem = Launcher.getEquippedWeapon(); // Store replaced item
                    allEquipments.add(Launcher.getEquippedWeapon());
                }
                Launcher.setEquippedWeapon((Weapon) retrievedEquipment);
                character.equipWeapon((Weapon) retrievedEquipment);
            } else if(retrievedEquipment.getClass().getSimpleName().equals("Armor")) {
                if (Launcher.getEquippedArmor() != null) {
                    replacedItem = Launcher.getEquippedArmor(); // Store replaced item
                    allEquipments.add(Launcher.getEquippedArmor());
                }
                Launcher.setEquippedArmor((Armor) retrievedEquipment);
                character.equipArmor((Armor) retrievedEquipment);
            }

            Launcher.setMainCharacter(character);
            Launcher.setAllEquipments(allEquipments);

            ImageView imgView = new ImageView();
            if (imgGroup.getChildren().size()!=1) {
                imgGroup.getChildren().remove(1);
            }
            lbl.setText(retrievedEquipment.getClass().getSimpleName() + ":\n" +
                    retrievedEquipment.getName());

            // Fixed image loading
            try {
                String imagePath = retrievedEquipment.getImagepath();
                Image image = new Image(AllCustomHandler.class.getClassLoader().getResourceAsStream(imagePath));
                imgView.setImage(image);
            } catch (Exception e) {
                System.err.println("Could not load equipment image: " + retrievedEquipment.getImagepath());
            }

            imgGroup.getChildren().add(imgView);
            dragCompleted = true;
        }
        event.setDropCompleted(dragCompleted);
    }

    // Add this new method to check if equipment can be equipped
    private static boolean canEquipItem(BasedCharacter character, BasedEquipment equipment) {
        // Check if character is BattleMage by checking the class name
        boolean isBattleMage = character.getClass().getSimpleName().equals("BattleMageCharacter");

        if (equipment instanceof Weapon) {
            Weapon weapon = (Weapon) equipment;

            // BattleMage can equip any weapon type
            if (isBattleMage) {
                return true;
            }

            // Other characters can only equip weapons that match their damage type
            return character.getType() == weapon.getDamageType();

        } else if (equipment instanceof Armor) {
            // BattleMage can now equip armor (removed the restriction)
            // All characters can equip armor
            return true;
        }

        return false;
    }

    // Modified onEquipDone to handle both successful and failed drops
    public static void onEquipDone(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
        BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(
                BasedEquipment.DATA_FORMAT);

        // Check if the drop was successful
        if (event.isDropCompleted()) {
            // Drop was successful on a valid target, remove the dragged item from inventory
            int pos = -1;
            for(int i = 0; i < allEquipments.size(); i++) {
                if (allEquipments.get(i).getName().equals(retrievedEquipment.getName())) {
                    pos = i;
                    break;
                }
            }
            if (pos != -1) {
                allEquipments.remove(pos);
            }
            // The replaced item stays in inventory (already added in onDragDropped)
        } else {
            // Drop failed (dropped outside valid equipment slot)
            // Remove any replaced item that might have been added to inventory
            if (replacedItem != null) {
                for (int i = allEquipments.size() - 1; i >= 0; i--) {
                    if (allEquipments.get(i).getName().equals(replacedItem.getName())) {
                        allEquipments.remove(i);
                        break;
                    }
                }
            }

            // Ensure the dragged item remains in inventory
            boolean itemInInventory = false;
            for (BasedEquipment equipment : allEquipments) {
                if (equipment.getName().equals(retrievedEquipment.getName())) {
                    itemInInventory = true;
                    break;
                }
            }

            // If item is not in inventory, add it back
            if (!itemInInventory) {
                allEquipments.add(retrievedEquipment);
            }
        }

        replacedItem = null; // Reset replaced item tracker
        Launcher.setAllEquipments(allEquipments);
        Launcher.refreshPane();
    }

    // New method to handle drag over for invalid drop zones
    public static void onInvalidDragOver(DragEvent event) {
        // Don't accept any transfer modes for invalid drop zones
        // This ensures the drop will fail and onEquipDone will handle returning to inventory
    }

    // New method to handle drops on invalid zones
    public static void onInvalidDragDropped(DragEvent event) {
        // Always set drop completed to false for invalid zones
        event.setDropCompleted(false);
    }
}
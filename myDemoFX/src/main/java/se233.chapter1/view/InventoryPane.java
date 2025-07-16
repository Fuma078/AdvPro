package se233.chapter1.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import java.util.ArrayList;
import se233.chapter1.Launcher;
import se233.chapter1.model.item.BasedEquipment;
import static se233.chapter1.controller.AllCustomHandler.onDragDetected;
import static se233.chapter1.controller.AllCustomHandler.onEquipDone;

public class InventoryPane extends ScrollPane {
    private ArrayList<BasedEquipment> equipmentArray;

    public InventoryPane() {}

    private Pane getDetailsPane() {
        Pane inventoryInfoPane = new HBox(10);
        inventoryInfoPane.setBorder(null);
        inventoryInfoPane.setPadding(new Insets(25, 25, 25, 25));

        if (equipmentArray != null && !equipmentArray.isEmpty()) {
            ImageView[] imageViewList = new ImageView[equipmentArray.size()];
            for(int i = 0; i < equipmentArray.size(); i++) {
                imageViewList[i] = new ImageView();
                imageViewList[i].setFitWidth(64);
                imageViewList[i].setFitHeight(64);
                imageViewList[i].setPreserveRatio(true);

                // Fixed image loading - using class loader
                try {
                    String imagePath = equipmentArray.get(i).getImagepath();
                    Image image = new Image(getClass().getClassLoader().getResourceAsStream(imagePath));
                    imageViewList[i].setImage(image);
                } catch (Exception e) {
                    System.err.println("Could not load equipment image: " + equipmentArray.get(i).getImagepath());
                    // Try to load a fallback image
                    try {
                        Image fallbackImage = new Image(getClass().getClassLoader().getResourceAsStream("se233.chapter1/assets/unknown.png"));
                        imageViewList[i].setImage(fallbackImage);
                    } catch (Exception ex) {
                        System.err.println("Could not load fallback image either");
                    }
                }

                final int finalI = i;
                imageViewList[i].setOnDragDetected(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        onDragDetected(event, equipmentArray.get(finalI), imageViewList[finalI]);
                    }
                });
                imageViewList[i].setOnDragDone(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        onEquipDone(event);
                    }
                });
            }
            inventoryInfoPane.getChildren().addAll(imageViewList);
        }
        return inventoryInfoPane;
    }

    public void drawPane(ArrayList<BasedEquipment> equipmentArray) {
        this.equipmentArray = equipmentArray;
        Pane inventoryInfo = getDetailsPane();
        this.setStyle("-fx-background-color:Red;");
        this.setContent(inventoryInfo);
    }
}
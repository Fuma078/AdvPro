package se233.chapter1.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter1.controller.AllCustomHandler;
import se233.chapter1.model.character.BasedCharacter;

public class CharacterPane extends ScrollPane {
    private BasedCharacter character;

    public CharacterPane() {
        this.setPrefWidth(300);
        this.setFitToWidth(true);
    }

    private Pane getDetailsPane() {
        Pane characterInfoPane = new VBox(10);
        characterInfoPane.setBorder(null);
        characterInfoPane.setPadding(new Insets(25, 25, 25, 25));

        Label name, type, hp, atk, def, res;
        ImageView mainImage = new ImageView();
        mainImage.setFitHeight(100);
        mainImage.setFitWidth(100);
        mainImage.setPreserveRatio(true);

        if (this.character != null) {
            name = new Label("Name: " + character.getName());

            // Fixed image loading approach
            try {
                String imagePath = character.getImagepath();
                System.out.println("Trying to load image: " + imagePath);

                // Try to load the image using class loader
                Image image = new Image(getClass().getClassLoader().getResourceAsStream(imagePath));

                if (image != null && !image.isError()) {
                    mainImage.setImage(image);
                    System.out.println("Successfully loaded image: " + imagePath);
                } else {
                    throw new Exception("Image is null or has error");
                }
            } catch (Exception e) {
                System.err.println("Could not load character image: " + character.getImagepath());
                System.err.println("Error: " + e.getMessage());

                // Try to load a fallback image
                try {
                    Image fallbackImage = new Image(getClass().getClassLoader().getResourceAsStream("se233.chapter1/assets/unknown.png"));
                    if (fallbackImage != null && !fallbackImage.isError()) {
                        mainImage.setImage(fallbackImage);
                        System.out.println("Loaded fallback image");
                    }
                } catch (Exception ex) {
                    System.err.println("Could not load fallback image either: " + ex.getMessage());
                }
            }

            hp = new Label("HP: " + character.getHp().toString() + "/" + character.getFullHp().toString());
            type = new Label("Type: " + character.getType().toString());
            atk = new Label("ATK: " + character.getPower());
            def = new Label("DEF: " + character.getDefense());
            res = new Label("RES: " + character.getResistance());
        } else {
            name = new Label("Name: -");

            // Try to load unknown image for null character
            try {
                Image unknownImage = new Image(getClass().getClassLoader().getResourceAsStream("se233.chapter1/assets/unknown.png"));
                if (unknownImage != null && !unknownImage.isError()) {
                    mainImage.setImage(unknownImage);
                }
            } catch (Exception e) {
                System.err.println("Could not load unknown.png: " + e.getMessage());
            }

            hp = new Label("HP: -");
            type = new Label("Type: -");
            atk = new Label("ATK: -");
            def = new Label("DEF: -");
            res = new Label("RES: -");
        }

        Button genCharacter = new Button();
        genCharacter.setText("Generate Character");
        genCharacter.setOnAction(new AllCustomHandler.GenCharacterHandler());

        characterInfoPane.getChildren().addAll(name, mainImage, type, hp, atk, def, res, genCharacter);
        return characterInfoPane;
    }

    public void drawPane(BasedCharacter character) {
        this.character = character;
        Pane characterInfo = getDetailsPane();
        this.setStyle("-fx-background-color:Red;");
        this.setContent(characterInfo);
    }
}
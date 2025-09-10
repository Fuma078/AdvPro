package se233.chapter4.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.chapter4.Launcher;
import se233.chapter4.model.GameCharacter;
import se233.chapter4.model.Keys;

public class GameStage extends Pane {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 400;
    public final static int GROUND = 300;
    private Image gameStageImg;
    private GameCharacter gameCharacter;
    private GameCharacter gameCharacter2; //ข้อ1
    private Keys keys;
    public GameStage() {
        keys = new Keys();
        gameStageImg = new Image(Launcher.class.getResourceAsStream("assets/Background.png"));
        ImageView backgroundImg = new ImageView(gameStageImg);
        backgroundImg.setFitHeight(HEIGHT);
        backgroundImg.setFitWidth(WIDTH);
        gameCharacter = new GameCharacter(30, GameStage.GROUND - 64, 0, 0, KeyCode.A,KeyCode.D,KeyCode.W);
        gameCharacter2 = new GameCharacter(GameStage.WIDTH - 60, GameStage.GROUND - 40, "assets/RockmanSheet.png", KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP); //ข้อ1
        getChildren().addAll(backgroundImg, gameCharacter, gameCharacter2); //เพิ่ม gameCharacter2 เข้าไป
    }

    public GameCharacter getGameCharacter() {
        return gameCharacter;
    }
    public GameCharacter getGameCharacter2() {
        return gameCharacter2;
    }
    public Keys getKeys() {
        return keys;
    }
}

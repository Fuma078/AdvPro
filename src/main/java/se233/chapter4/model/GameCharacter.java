package se233.chapter4.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.chapter4.Launcher;
import se233.chapter4.view.GameStage;

public class GameCharacter extends Pane {
    private static final Logger logger = LogManager.getLogger(GameCharacter.class);
    private Image gameCharacterImg;
    private AnimatedSprite imageView;
    private int x;
    private int y;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode upKey;
    int xVelocity = 0;
    boolean isMoveLeft = false;
    boolean isMoveRight = false;
    int yVelocity = 0;
    boolean isFalling = true;
    boolean canJump = false;
    boolean isJumping = false;
    int xAcceleration = 1;
    int yAcceleration = 1;
    int xMaxVelocity = 7;
    int yMaxVelocity = 17;

    public GameCharacter(int x, int y, int offsetX, int offsetY, KeyCode leftKey, KeyCode rightKey, KeyCode upKey) {
        this.x = x;
        this.y = y;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.gameCharacterImg = new Image(Launcher.class.getResourceAsStream("assets/MarioSheet.png"));
        this.imageView = new AnimatedSprite(gameCharacterImg, 4, 4, 1, offsetX, offsetX, 16, 32);
        this.imageView.setFitWidth(16);
        this.imageView.setFitHeight(32);
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.getChildren().addAll(this.imageView);
    }

    public GameCharacter(int x, int y, String spriteSheetPath, KeyCode leftKey, KeyCode rightKey, KeyCode upKey) {
        this.x = x;
        this.y = y;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.gameCharacterImg = new Image(Launcher.class.getResourceAsStream(spriteSheetPath));
        this.imageView = new AnimatedSprite(gameCharacterImg, 10, 5, 2, 0, 0, 541, 514);
        this.imageView.setFitWidth(58);
        this.imageView.setFitHeight(50.0);
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.xMaxVelocity = 10;
        this.yMaxVelocity = 20;
        this.getChildren().addAll(this.imageView);
    }
    public void moveX() {
        setTranslateX(x);
        if(isMoveLeft) {
            xVelocity = xVelocity >= xMaxVelocity ? xMaxVelocity : xVelocity + xAcceleration;
            x = x - xVelocity; }
        if(isMoveRight) {
            xVelocity = xVelocity >= xMaxVelocity ? xMaxVelocity : xVelocity + xAcceleration;
            x = x + xVelocity; }
    }
    public void moveY() {
        setTranslateY(y);
        if(isFalling) {
            yVelocity = yVelocity >= yMaxVelocity ? yMaxVelocity : yVelocity + yAcceleration;
            y = y + yVelocity;
        } else if(isJumping) {
            yVelocity = yVelocity <= 0 ? 0: yVelocity - yAcceleration;
            y = y - yVelocity;
        }
    }
    public void checkReachGameWall() {
        if(x <= 0) {
            x = 0;
            logger.debug("Character at x = 0. Collided with the left game boundary.");
        } else if (x + this.getImageView().getFitWidth() >= GameStage.WIDTH) {
            x = GameStage.WIDTH - (int)this.getImageView().getFitWidth();
            logger.debug("Character at x = {}. Collided with the right game boundary.", x);
        }
    }
    public void jump() {
        if (canJump) {
            yVelocity = yMaxVelocity;
            canJump = false;
            isJumping = true;
            isFalling = false;
        }
    }
    public void checkReachHighest () {
        if(isJumping && yVelocity <= 0) {
            isJumping = false;
            isFalling = true;
            yVelocity = 0;
        }
    }
    public void checkReachFloor() {
        if (isFalling && y >= GameStage.GROUND - this.getImageView().getFitHeight()) {
            isFalling = false;
            canJump = true;
            yVelocity = 0;
        }
    }
    public void repaint() {
        moveX();
        moveY();
    }
    public void moveLeft() {
        setScaleX(-1);
        isMoveLeft = true;
        isMoveRight = false;
    }
    public void moveRight() {
        setScaleX(1);
        isMoveLeft = false;
        isMoveRight = true;
    }
    public void stop() {
        isMoveLeft = false;
        isMoveRight = false;
    }
    public void trace() {
        logger.info("x:{} y:{} vx:{} vy:{}",x,y,xVelocity,yVelocity);
    }
    public KeyCode getLeftKey() {
        return leftKey;
    }

    public KeyCode getRightKey() {
        return rightKey;
    }

    public KeyCode getUpKey() {
        return upKey;
    }
    public AnimatedSprite getImageView() {
        return imageView;
    }

}

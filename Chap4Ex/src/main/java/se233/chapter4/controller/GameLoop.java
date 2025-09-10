package se233.chapter4.controller;

import se233.chapter4.model.GameCharacter;
import se233.chapter4.view.GameStage;

import java.util.ArrayList;

public class GameLoop implements Runnable {
    private GameStage gameStage;
    private int frameRate;
    private float interval;
    private boolean running;
    private ArrayList<GameCharacter> characters;
    public GameLoop(GameStage gameStage) {
        this.gameStage = gameStage;
        frameRate = 50;
        interval = 1000.0f / frameRate;
        running = true;
        this.characters = new ArrayList<>();
        this.characters.add(gameStage.getGameCharacter());
        this.characters.add(gameStage.getGameCharacter2());
    }
    private void update(GameCharacter gameCharacter) {
        boolean leftPressed = gameStage.getKeys().isPressed(gameCharacter.getLeftKey());
        boolean rightPressed = gameStage.getKeys().isPressed(gameCharacter.getRightKey());
        boolean upPressed = gameStage.getKeys().isPressed(gameCharacter.getUpKey());

        gameCharacter.stop();

        if (leftPressed && rightPressed) {

        } else if (leftPressed) {
            gameCharacter.getImageView().tick();
            gameCharacter.moveLeft();
            gameCharacter.trace();
        } else if (rightPressed) {
            gameCharacter.getImageView().tick();
            gameCharacter.moveRight();
            gameCharacter.trace();
        } else {
            gameCharacter.stop();
        }

        if (upPressed) {
            gameCharacter.jump();
            gameCharacter.trace();
        }
        gameCharacter.checkReachGameWall();
        gameCharacter.checkReachHighest();
        gameCharacter.checkReachFloor();
        gameCharacter.repaint();
    }
    @Override
    public void run() {
        while (running) {
            float time = System.currentTimeMillis();
            // Update(gameStage.getGameCharacter());
            time = System.currentTimeMillis() - time;

            for (GameCharacter character : characters) {
                update(character);
            }

            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep((long) (interval - (interval % time)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

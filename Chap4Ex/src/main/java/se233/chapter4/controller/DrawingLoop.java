package se233.chapter4.controller;

import se233.chapter4.model.GameCharacter;
import se233.chapter4.view.GameStage;

import java.util.ArrayList;

public class DrawingLoop implements Runnable {
    private GameStage gameStage;
    private int frameRate;
    private float interval;
    private boolean running;
    private ArrayList<GameCharacter> characters;
    public DrawingLoop(GameStage gameStage) {
        this.gameStage = gameStage;
        frameRate = 60;
        interval = 1000.0f / frameRate; // 1000 ms = 1 second.
        running = true;

        this.characters = new ArrayList<>();
        this.characters.add(gameStage.getGameCharacter());
        this.characters.add(gameStage.getGameCharacter2());
    }
    private void checkDrawCollisions(GameCharacter gameCharacter) {
        gameCharacter.checkReachGameWall();
        gameCharacter.checkReachHighest();
        gameCharacter.checkReachFloor();
    }
    private void paint(GameCharacter gameCharacter) {
    }
    @Override
    public void run(){
        while (running) {
            float time = System.currentTimeMillis();
            checkDrawCollisions(gameStage.getGameCharacter());
            paint(gameStage.getGameCharacter());
            time = System.currentTimeMillis() - time;

            for (GameCharacter character : characters) {
                checkDrawCollisions(character);
                paint(character);
            }

            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException e) {

                }
            } else {
               try {
                   Thread.sleep((long) (interval - time));
               } catch (InterruptedException e) {

               }
            }
        }
    }
}

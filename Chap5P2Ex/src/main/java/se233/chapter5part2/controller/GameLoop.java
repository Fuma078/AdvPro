package se233.chapter5part2.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import se233.chapter5part2.model.Direction;
import se233.chapter5part2.model.Food;
import se233.chapter5part2.model.Snake;
import se233.chapter5part2.view.GameStage;

public class GameLoop implements Runnable {
    private GameStage gameStage;
    private Snake snake;
    private Food food;
    private float interval = 1000.0f / 10;
    private boolean running;
    private int score = 0;
    private boolean isTestMode = false; // Add test mode flag

    public GameLoop(GameStage gameStage, Snake snake, Food food) {
        this.snake = snake;
        this.gameStage = gameStage;
        this.food = food;
        running = true;

        // Detect if we're in test mode by checking if JavaFX is available
        try {
            Platform.isFxApplicationThread();
        } catch (Exception e) {
            isTestMode = true;
        }
    }

    // Constructor for test mode
    public GameLoop(GameStage gameStage, Snake snake, Food food, boolean testMode) {
        this.snake = snake;
        this.gameStage = gameStage;
        this.food = food;
        running = true;
        this.isTestMode = testMode;
    }

    private void keyProcess() {
        KeyCode curKey = gameStage.getKey();
        if (curKey == KeyCode.UP)
            snake.setDirection(Direction.UP);
        else if (curKey == KeyCode.DOWN)
            snake.setDirection(Direction.DOWN);
        else if (curKey == KeyCode.LEFT)
            snake.setDirection(Direction.LEFT);
        else if (curKey == KeyCode.RIGHT)
            snake.setDirection(Direction.RIGHT);
        snake.move();
    }

    private void checkCollision() {
        if (snake.collided(food)) {
            snake.grow();
            score += food.getPoints(); // Award points based on food type
            food.respawn();
        }
        if (snake.checkDead()) {
            running = false;
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        if (isTestMode) {
            // In test mode, just print to console instead of showing dialog
            System.out.println("Game Over! Final Score: " + score);
            return;
        }

        try {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText("Snake Game Ended!");
                alert.setContentText("Final Score: " + score);
                alert.showAndWait();
            });
        } catch (Exception e) {
            // If JavaFX is not available, just print to console
            System.out.println("Game Over! Final Score: " + score);
        }
    }

    private void redraw() {
        gameStage.render(snake, food);
    }

    public int getScore() {
        return score;
    }

    @Override
    public void run() {
        while (running) {
            keyProcess();
            checkCollision();
            redraw();
            try {
                Thread.sleep((long)interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
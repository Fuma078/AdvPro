package se233.chapter5part2.model;

import javafx.geometry.Point2D;
import se233.chapter5part2.view.GameStage;

import java.util.Random;

public class Food {
    private Point2D position;
    private Random rn;
    private FoodType foodType;
    private int points;

    public enum FoodType {
        STANDARD, SPECIAL
    }

    public Food(Point2D position) {
        this.rn = new Random();
        this.position = position;
        this.foodType = FoodType.STANDARD;
        this.points = 1;
    }

    public Food() {
        this.rn = new Random();
        this.position = new Point2D(rn.nextInt(GameStage.WIDTH), rn.nextInt(GameStage.HEIGHT));
        this.foodType = FoodType.STANDARD;
        this.points = 1;
    }

    public Food(Point2D position, FoodType foodType) {
        this.rn = new Random();
        this.position = position;
        this.foodType = foodType;
        this.points = (foodType == FoodType.SPECIAL) ? 5 : 1;
    }

    public void respawn() {
        Point2D prev_position = this.position;
        do {
            this.position = new Point2D(rn.nextInt(GameStage.WIDTH), rn.nextInt(GameStage.HEIGHT));
        } while (prev_position.equals(this.position));

        // 10% chance to spawn special food
        this.foodType = (rn.nextInt(10) == 0) ? FoodType.SPECIAL : FoodType.STANDARD;
        this.points = (foodType == FoodType.SPECIAL) ? 5 : 1;
    }

    public Point2D getPosition() {
        return position;
    }

    public int getPoints() {
        return points;
    }

    public FoodType getFoodType() {
        return foodType;
    }
}
package se233.chapter6.model;

import javafx.geometry.Point2D;
import se233.chapter6.view.GameStage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Food {

    private static final Logger logger = LogManager.getLogger(Food.class);
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
        logger.info("food: x:{} y:{}",this.position.getX(), this.position.getY());

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
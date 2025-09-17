package se233.chapter5part2;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.chapter5part2.model.Food;

import static org.junit.jupiter.api.Assertions.*;

public class FoodTest {
    private Food food;

    @BeforeEach
    public void setup(){
        food = new Food(new Point2D(0,0));
    }
    @Test
    public void respawn_shouldBeOnNewPosition() {
        Point2D originalPosition = food.getPosition();
        food.respawn();
        assertNotEquals(food.getPosition(), originalPosition);
    }
    @Test
    public void constructor_withoutPosition_shouldCreateRandomPosition() {
        Food randomFood = new Food();
        assertNotNull(randomFood.getPosition());
        assertTrue(randomFood.getPosition().getX() >= 0);
        assertTrue(randomFood.getPosition().getY() >= 0);
    }
    @Test
    public void getPosition_shouldReturnCorrectPosition() {
        Point2D expectedPosition = new Point2D(5, 10);
        Food testFood = new Food(expectedPosition);
        assertEquals(expectedPosition, testFood.getPosition());
    }
}
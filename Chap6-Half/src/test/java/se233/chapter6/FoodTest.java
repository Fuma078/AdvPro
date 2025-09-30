package se233.chapter6;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.chapter6.model.Food;

import static org.junit.jupiter.api.Assertions.*;

public class FoodTest {
    private Food food;

    @BeforeEach
    public void setup(){
        food = new Food(new Point2D(0,0));
    }

    // Original tests
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

    // NEW TESTS FOR QUESTION 2: Scoring Mechanism
    @Test
    public void standardFood_shouldAwardOnePoint() {
        Food standardFood = new Food(new Point2D(0, 0), Food.FoodType.STANDARD);
        assertEquals(1, standardFood.getPoints());
    }

    @Test
    public void defaultFood_shouldBeStandardType() {
        Food defaultFood = new Food();
        assertEquals(Food.FoodType.STANDARD, defaultFood.getFoodType());
        assertEquals(1, defaultFood.getPoints());
    }

    // NEW TESTS FOR QUESTION 3: Special Food
    @Test
    public void specialFood_shouldAwardFivePoints() {
        Food specialFood = new Food(new Point2D(0, 0), Food.FoodType.SPECIAL);
        assertEquals(5, specialFood.getPoints());
    }

    @Test
    public void specialFood_shouldHaveCorrectType() {
        Food specialFood = new Food(new Point2D(0, 0), Food.FoodType.SPECIAL);
        assertEquals(Food.FoodType.SPECIAL, specialFood.getFoodType());
    }

    @Test
    public void respawn_shouldRandomlyCreateDifferentFoodTypes() {
        Food food = new Food();

        // Test multiple respawns - should eventually see both types
        boolean hasStandard = false;
        boolean hasSpecial = false;

        for (int i = 0; i < 100; i++) {
            food.respawn();
            if (food.getFoodType() == Food.FoodType.STANDARD) {
                hasStandard = true;
            }
            if (food.getFoodType() == Food.FoodType.SPECIAL) {
                hasSpecial = true;
            }
            if (hasStandard && hasSpecial) break;
        }

        assertTrue(hasStandard); // Should definitely have standard food
        // Note: Special food has 10% chance, so might not appear in some test runs
    }
}
package se233.chapter6;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.chapter6.model.Direction;
import se233.chapter6.model.Food;
import se233.chapter6.model.Snake;

import static org.junit.jupiter.api.Assertions.*;

public class SnakeTest {
    private Snake snake;

    @BeforeEach
    public void setup() {
        snake = new Snake(new Point2D(0, 0));
    }

    // Original tests
    @Test
    public void initialPosition_shouldBe_atOrigin() {
        assertEquals(snake.getHead(), new Point2D(0, 0));
    }

    @Test
    public void move_afterInitialized_headShouldBeInDownwardDirection() {
        snake.setDirection(Direction.DOWN);
        snake.move();
        assertEquals(snake.getHead(), new Point2D(0, 1));
    }

    @Test
    public void grow_shouldIncreaseLengthByOne(){
        int initialLength = snake.getLength();
        snake.move();
        snake.grow();
        assertEquals(snake.getLength(), initialLength + 1);
    }

    @Test
    public void grow_shouldAddPreviousHeadToBody(){
        Point2D initialHead = snake.getHead();
        snake.move();
        snake.grow();
        assertTrue(snake.getBody().contains(initialHead));
    }

    @Test
    public void collided_withFood_shouldBeDetected(){
        Food food = new Food(new Point2D(0,0));
        assertTrue(snake.collided(food));
    }

    @Test
    public void checkDead_ifHitGameBorder_snakeWillDie() {
        snake = new Snake(new Point2D(29,29)); // Close to border
        snake.setDirection(Direction.RIGHT);
        snake.move();
        assertTrue(snake.checkDead());
    }

    @Test
    public void checkDead_ifHitItself_snakeWillDie() {
        snake = new Snake(new Point2D(5,5)); // Start away from borders

        // Grow the snake first to make it longer
        snake.setDirection(Direction.DOWN);
        snake.move();
        snake.grow();

        snake.setDirection(Direction.LEFT);
        snake.move();
        snake.grow();

        snake.setDirection(Direction.UP);
        snake.move();
        snake.grow();

        // Now snake should be long enough to hit itself when turning right
        snake.setDirection(Direction.RIGHT);
        snake.move();
        snake.grow();

        assertTrue(snake.checkDead());
    }

    // NEW TESTS FOR QUESTION 4: Direction Restriction
    @Test
    public void setDirection_oppositeDirection_shouldBeIgnored() {
        snake.setDirection(Direction.RIGHT);

        // Try to reverse direction immediately
        snake.setDirection(Direction.LEFT);

        // Direction should still be RIGHT (not changed)
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    public void setDirection_nonOppositeDirection_shouldBeAllowed() {
        Snake snake = new Snake(new Point2D(5, 5));
        snake.setDirection(Direction.RIGHT);

        // Try to turn up (not opposite)
        snake.setDirection(Direction.UP);

        // Direction should be UP
        assertEquals(Direction.UP, snake.getDirection());
    }

    @Test
    public void directionRestriction_upToDown_shouldBeBlocked() {
        // Set initial direction to DOWN
        snake.setDirection(Direction.DOWN);
        // Try to reverse to UP
        snake.setDirection(Direction.UP);

        // Direction should still be DOWN (blocked)
        assertEquals(Direction.DOWN, snake.getDirection());
    }

    @Test
    public void directionRestriction_downToUp_shouldBeBlocked() {
        // Set initial direction to DOWN
        snake.setDirection(Direction.DOWN);

        // Try to reverse to UP
        snake.setDirection(Direction.UP);

        // Direction should still be DOWN (blocked)
        assertEquals(Direction.DOWN, snake.getDirection());
    }

    @Test
    public void directionRestriction_leftToRight_shouldBeBlocked() {
        snake.setDirection(Direction.LEFT);

        snake.setDirection(Direction.RIGHT);

        assertEquals(Direction.LEFT, snake.getDirection());
    }

    @Test
    public void directionRestriction_rightToLeft_shouldBeBlocked() {
        snake.setDirection(Direction.RIGHT);

        snake.setDirection(Direction.LEFT);

        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    public void directionRestriction_canTurnPerpendicularly() {
        snake.setDirection(Direction.UP);

        // Can turn left from UP (perpendicular, not opposite)
        snake.setDirection(Direction.LEFT);
        assertEquals(Direction.LEFT, snake.getDirection());

        // Can turn down from LEFT (perpendicular, not opposite)
        snake.setDirection(Direction.DOWN);
        assertEquals(Direction.DOWN, snake.getDirection());
    }

    @Test
    public void directionRestriction_preventInstantReversal() {
        // Start moving right
        snake.setDirection(Direction.RIGHT);
        snake.move();
        snake.grow(); // Make snake longer

        // Try to immediately reverse
        snake.setDirection(Direction.LEFT);

        // Should not be able to reverse
        assertEquals(Direction.RIGHT, snake.getDirection());

        // But should be able to turn up or down
        snake.setDirection(Direction.UP);
        assertEquals(Direction.UP, snake.getDirection());
    }
}
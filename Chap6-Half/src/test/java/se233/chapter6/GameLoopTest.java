package se233.chapter6;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se233.chapter6.controller.GameLoop;
import se233.chapter6.model.Direction;
import se233.chapter6.model.Food;
import se233.chapter6.model.Snake;
import se233.chapter6.view.GameStage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameLoopTest {
    private GameStage gameStage;
    private Snake snake;
    private Food food;
    private GameLoop gameLoop;

    @BeforeEach
    public void setUp(){
        gameStage = new GameStage();
        snake = new Snake(new Point2D(0,0));
        food = new Food(new Point2D(0,1));
        gameLoop = new GameLoop(gameStage, snake, food);
    }

    private void clockTickHelper() throws Exception {
        ReflectionHelper.invokeMethod(gameLoop, "keyProcess", new Class<?>[0]);
        ReflectionHelper.invokeMethod(gameLoop, "checkCollision", new Class<?>[0]);
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[0]);
    }

    // Original tests
    @Test
    public void keyProcess_pressRight_snakeTurnRight() throws Exception {
        ReflectionHelper.setField(gameStage, "key", KeyCode.RIGHT);
        ReflectionHelper.setField(snake, "direction", Direction.DOWN);

        ReflectionHelper.invokeMethod(gameLoop, "keyProcess", new Class<?>[0]);

        Direction currentDirection = (Direction) ReflectionHelper.getField(snake, "direction");
        assertEquals(Direction.RIGHT, currentDirection);
    }

    @Test
    public void collided_snakeEatFood_shouldGrow() throws Exception {
        int initialLength = snake.getLength();
        Point2D initialFoodPosition = food.getPosition();

        clockTickHelper();

        assertTrue(snake.getLength() > initialLength);
        clockTickHelper();
        assertNotEquals(food.getPosition(), initialFoodPosition);
    }

    @Test
    public void collided_snakeHitBorder_shouldDie() throws Exception{
        ReflectionHelper.setField(gameStage, "key", KeyCode.LEFT);
        ReflectionHelper.setField(snake, "direction", Direction.LEFT);

        clockTickHelper();

        Boolean running = (Boolean) ReflectionHelper.getField(gameLoop, "running");
        assertFalse(running);
    }

    @Test
    public void redraw_calledThreeTimes_snakeAndFoodShouldRenderThreeTimes() throws Exception {
        GameStage mockGameStage = Mockito.mock(GameStage.class);
        Snake mockSnake = Mockito.mock(Snake.class);
        Food mockFood = Mockito.mock(Food.class);
        GameLoop gameLoop = new GameLoop(mockGameStage, mockSnake, mockFood);
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[]{});
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[]{});
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[]{});
        verify(mockGameStage, times(3)).render(mockSnake, mockFood);
    }

    // NEW TESTS FOR QUESTION 1: Game Over Dialog (simplified - no Platform.runLater)
    @Test
    public void gameOver_shouldStopRunning_whenSnakeHitsBorder() throws Exception {
        // Move snake to border position that will cause death
        ReflectionHelper.setField(snake, "head", new Point2D(-1, 0));

        // Process collision check
        ReflectionHelper.invokeMethod(gameLoop, "checkCollision", new Class<?>[0]);

        Boolean running = (Boolean) ReflectionHelper.getField(gameLoop, "running");
        assertFalse(running); // Game should stop
    }

    // NEW TESTS FOR QUESTION 2: Scoring Mechanism
    @Test
    public void scoring_initialScore_shouldBeZero() {
        assertEquals(0, gameLoop.getScore());
    }

    @Test
    public void scoring_snakeEatsStandardFood_scoreIncreasesBy1() throws Exception {
        int initialScore = gameLoop.getScore();

        clockTickHelper();

        assertEquals(initialScore + 1, gameLoop.getScore());
    }

    @Test
    public void scoring_snakeEatsSpecialFood_scoreIncreasesBy5() throws Exception {
        Food specialFood = new Food(new Point2D(0, 1), Food.FoodType.SPECIAL);
        GameLoop specialGameLoop = new GameLoop(gameStage, snake, specialFood);

        int initialScore = specialGameLoop.getScore();

        ReflectionHelper.invokeMethod(specialGameLoop, "keyProcess", new Class<?>[0]);
        ReflectionHelper.invokeMethod(specialGameLoop, "checkCollision", new Class<?>[0]);

        assertEquals(initialScore + 5, specialGameLoop.getScore());
    }

    @Test
    public void scoring_multipleFood_shouldAccumulate() throws Exception {
        int initialScore = gameLoop.getScore();

        // Eat first food
        clockTickHelper();
        assertEquals(initialScore + 1, gameLoop.getScore());

        // Position snake to eat another food
        ReflectionHelper.setField(snake, "head", food.getPosition());
        ReflectionHelper.invokeMethod(gameLoop, "checkCollision", new Class<?>[0]);

        assertEquals(initialScore + 2, gameLoop.getScore());
    }
}
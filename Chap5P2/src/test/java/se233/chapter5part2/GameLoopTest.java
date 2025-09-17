package se233.chapter5part2;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se233.chapter5part2.controller.GameLoop;
import se233.chapter5part2.model.Direction;
import se233.chapter5part2.model.Food;
import se233.chapter5part2.model.Snake;
import se233.chapter5part2.view.GameStage;

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

    @Test
    public void keyProcess_pressRight_snakeTurnRight() throws Exception {
        ReflectionHelper.setField(gameStage, "key", KeyCode.RIGHT);
        ReflectionHelper.setField(snake, "direction", Direction.DOWN);

        // Only call keyProcess for this test
        ReflectionHelper.invokeMethod(gameLoop, "keyProcess", new Class<?>[0]);

        Direction currentDirection = (Direction) ReflectionHelper.getField(snake, "direction");
        assertEquals(Direction.RIGHT, currentDirection);
    }

    @Test
    public void collided_snakeEatFood_shouldGrow() throws Exception {
        int initialLength = snake.getLength();
        Point2D initialFoodPosition = food.getPosition();

        clockTickHelper();

        // Snake should have grown
        assertTrue(snake.getLength() > initialLength);

        // Food should have respawned to a different position
        clockTickHelper();
        assertNotEquals(food.getPosition(), initialFoodPosition);
    }

    @Test
    public void collided_snakeHitBorder_shouldDie() throws Exception{
        // Move snake to position (0,0) and set direction LEFT to hit border
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
        GameLoop testGameLoop = new GameLoop(mockGameStage, mockSnake, mockFood);

        ReflectionHelper.invokeMethod(testGameLoop, "redraw", new Class<?>[0]);
        ReflectionHelper.invokeMethod(testGameLoop, "redraw", new Class<?>[0]);
        ReflectionHelper.invokeMethod(testGameLoop, "redraw", new Class<?>[0]);

        verify(mockGameStage, times(3)).render(mockSnake, mockFood);
    }
}
package se233.chapter5part1;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.chapter5part1.model.GameCharacter;
import se233.chapter5part1.view.GameStage;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class GameCharacterTest {
    private Field xVelocityField, yVelocityField, yAccelerationField;
    private GameCharacter gameCharacter;

    @BeforeAll
    public static void initJfxRuntime() {
        javafx.application.Platform.startup(() -> {
        });
    }

    @BeforeEach
    public void setup() throws NoSuchFieldException {
        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character1.png", 4, 3
                , 2, 111, 97, KeyCode.A, KeyCode.D, KeyCode.W);
        xVelocityField = gameCharacter.getClass().getDeclaredField("xVelocity");
        yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");
        yAccelerationField = gameCharacter.getClass().getDeclaredField("yAcceleration");
        xVelocityField.setAccessible(true);
        yVelocityField.setAccessible(true);
        yAccelerationField.setAccessible(true);
    }

    @Test
    public void moveX_givenMoveRightOnce_thenXCoordinateIncreasedByXVelocity() throws
            IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveRight();
        gameCharacter.moveX();
        assertEquals(30 + xVelocityField.getInt(gameCharacter), gameCharacter.getX(), "Move right x");
    }

    @Test
    public void moveY_givenTwoConsecutiveCalls_thenYVelocityIncreases() throws
            IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveY();
        int yVelocity1 = yVelocityField.getInt(gameCharacter);
        gameCharacter.moveY();
        int yVelocity2 = yVelocityField.getInt(gameCharacter);
        assertTrue(yVelocity2 > yVelocity1, "Velocity is increasing");
    }

    @Test
    public void moveY_givenTwoConsecutiveCalls_thenYAccelerationUnchanged() throws
            IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveY();
        int yAcceleration1 = yAccelerationField.getInt(gameCharacter);
        gameCharacter.moveY();
        int yAcceleration2 = yAccelerationField.getInt(gameCharacter);
        assertEquals(yAcceleration1, yAcceleration2, "Acceleration is not changed");
    }

    @Test
    public void respawn_givenNewGameCharacter_thenCoordinatesAre30_30() {
        gameCharacter.respawn();
        assertEquals(30, gameCharacter.getX(), "Initial x");
        assertEquals(30, gameCharacter.getY(), "Initial y");
    }

    @Test
    public void respawn_givenNewGameCharacter_thenScoreIs0() {
        gameCharacter.respawn();
        assertEquals(0, gameCharacter.getScore(), "Initial score");
    }

    // Part 1.1: Test cases for checkReachGameWall() method
    @Test
    public void checkReachGameWall_givenCharacterAtLeftBoundary_thenXCoordinateIs0() throws NoSuchFieldException, IllegalAccessException {
        // Access the private x field to set it directly
        Field xField = gameCharacter.getClass().getDeclaredField("x");
        xField.setAccessible(true);

        // Set character position to beyond left boundary
        xField.setInt(gameCharacter, -10);

        // Call the method to test
        gameCharacter.checkReachGameWall();

        // Verify that x coordinate is corrected to 0
        assertEquals(0, gameCharacter.getX(), "Character should be at left boundary (x=0)");
    }

    @Test
    public void checkReachGameWall_givenCharacterAtRightBoundary_thenXCoordinateIsWithinBounds() throws NoSuchFieldException, IllegalAccessException {
        // Access the private x field to set it directly
        Field xField = gameCharacter.getClass().getDeclaredField("x");
        xField.setAccessible(true);

        // Set character position to beyond right boundary
        xField.setInt(gameCharacter, GameStage.WIDTH + 10);

        // Call the method to test
        gameCharacter.checkReachGameWall();

        // Verify that x coordinate is corrected to be within bounds
        assertEquals(GameStage.WIDTH - gameCharacter.getCharacterWidth(), gameCharacter.getX(),
                "Character should be at right boundary within game width");
    }

    // Part 1.2: Test cases for jump() method
    @Test
    public void jump_givenCanJumpIsTrue_thenJumpIsSuccessfullyInitiated() throws NoSuchFieldException, IllegalAccessException {
        gameCharacter.respawn();

        // Access private fields
        Field canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        Field isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        canJumpField.setAccessible(true);
        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);

        // Set canJump to true (simulate character on ground)
        canJumpField.setBoolean(gameCharacter, true);
        isFallingField.setBoolean(gameCharacter, false);

        // Call jump method
        gameCharacter.jump();

        // Verify jump was initiated successfully
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should be false after jumping");
        assertTrue(isJumpingField.getBoolean(gameCharacter), "isJumping should be true after jumping");
        assertFalse(isFallingField.getBoolean(gameCharacter), "isFalling should be false when jumping");
        assertTrue(yVelocityField.getInt(gameCharacter) > 0, "yVelocity should be positive when jumping");
    }

    @Test
    public void jump_givenCharacterIsAlreadyAirborne_thenJumpIsNotPermitted() throws NoSuchFieldException, IllegalAccessException {
        gameCharacter.respawn();

        // Access private fields
        Field canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        Field isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        canJumpField.setAccessible(true);
        isJumpingField.setAccessible(true);
        isFallingField.setAccessible(true);

        // Set character as already airborne (canJump = false)
        canJumpField.setBoolean(gameCharacter, false);
        isJumpingField.setBoolean(gameCharacter, false);
        isFallingField.setBoolean(gameCharacter, true);

        int initialYVelocity = yVelocityField.getInt(gameCharacter);

        // Call jump method
        gameCharacter.jump();

        // Verify jump was not initiated
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should remain false");
        assertFalse(isJumpingField.getBoolean(gameCharacter), "isJumping should remain false");
        assertTrue(isFallingField.getBoolean(gameCharacter), "isFalling should remain true");
        assertEquals(initialYVelocity, yVelocityField.getInt(gameCharacter), "yVelocity should not change");
    }

    // Part 1.3: Test cases for collided() method
    @Test
    public void collided_givenHorizontalCollisionFromLeft_thenCharacterStopsAndPositionAdjusted() throws NoSuchFieldException, IllegalAccessException {
        // Create second character for collision
        GameCharacter otherCharacter = new GameCharacter(1, 150, 30, "assets/Character2.png", 4, 4, 1, 129, 66, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP);

        // Access private fields
        Field xField = gameCharacter.getClass().getDeclaredField("x");
        Field isMoveLeftField = gameCharacter.getClass().getDeclaredField("isMoveLeft");
        Field isMoveRightField = gameCharacter.getClass().getDeclaredField("isMoveRight");
        xField.setAccessible(true);
        isMoveLeftField.setAccessible(true);
        isMoveRightField.setAccessible(true);

        // Set up collision scenario: gameCharacter moving right towards otherCharacter
        xField.setInt(gameCharacter, 140); // Position gameCharacter to the left of otherCharacter
        isMoveRightField.setBoolean(gameCharacter, true);
        isMoveLeftField.setBoolean(gameCharacter, false);

        // Call collided method
        boolean result = gameCharacter.collided(otherCharacter);

        // Verify horizontal collision handling
        assertFalse(result, "Should return false for horizontal collision");
        assertFalse(isMoveRightField.getBoolean(gameCharacter), "Character should stop moving right");
        assertFalse(isMoveLeftField.getBoolean(gameCharacter), "Character should not be moving left");
        assertTrue(gameCharacter.getX() <= otherCharacter.getX() - gameCharacter.getCharacterWidth(),
                "Character position should be adjusted to prevent overlap");
    }

    @Test
    public void collided_givenVerticalCollisionFromAbove_thenScoreIncreasesAndCharacterLands() throws NoSuchFieldException, IllegalAccessException {
        // Create second character for collision with correct starting position to match test expectations
        GameCharacter otherCharacter = new GameCharacter(1, GameStage.WIDTH-160, 30, "assets/Character2.png", 4, 4, 1, 129, 66, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP);

        // Access private fields for otherCharacter to set its position for collision
        Field otherXField = otherCharacter.getClass().getDeclaredField("x");
        Field otherYField = otherCharacter.getClass().getDeclaredField("y");
        otherXField.setAccessible(true);
        otherYField.setAccessible(true);
        otherXField.setInt(otherCharacter, 150);
        otherYField.setInt(otherCharacter, 100);

        // Access private fields for gameCharacter
        Field xField = gameCharacter.getClass().getDeclaredField("x");
        Field yField = gameCharacter.getClass().getDeclaredField("y");
        Field isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        xField.setAccessible(true);
        yField.setAccessible(true);
        isFallingField.setAccessible(true);

        // Set up vertical collision scenario: gameCharacter falling onto otherCharacter
        xField.setInt(gameCharacter, 150); // Same x position as otherCharacter
        yField.setInt(gameCharacter, 50); // Above otherCharacter (y < otherCharacter.y)
        isFallingField.setBoolean(gameCharacter, true); // Must be falling

        int initialScore = gameCharacter.getScore();

        // Call collided method
        boolean result = gameCharacter.collided(otherCharacter);

        // Verify vertical collision handling
        assertTrue(result, "Should return true for vertical collision");
        assertEquals(initialScore + 1, gameCharacter.getScore(), "Score should increase by 1");

        // After collision and respawn, both characters should be back at their starting positions
        assertEquals(30, gameCharacter.getX(), "GameCharacter should respawn at starting X position");
        assertEquals(30, gameCharacter.getY(), "GameCharacter should respawn at starting Y position");
        assertEquals(GameStage.WIDTH-160, otherCharacter.getX(), "OtherCharacter should respawn at starting X position");
        assertEquals(30, otherCharacter.getY(), "OtherCharacter should respawn at starting Y position");
    }
}
package se233.chapter5part1;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.chapter5part1.model.Keys;

import static org.junit.jupiter.api.Assertions.*;

public class KeysTest {
    private Keys keys;

    @BeforeEach
    public void setup() {
        keys = new Keys();
    }

    @Test
    public void isPressed_givenSingleKeyPress_thenStateChangesToTrue() {
        // Initially, key should not be pressed
        assertFalse(keys.isPressed(KeyCode.A), "Key A should not be pressed initially");

        // Add the key (simulate key press)
        keys.add(KeyCode.A);

        // Verify key is now pressed
        assertTrue(keys.isPressed(KeyCode.A), "Key A should be pressed after add()");
    }

    @Test
    public void isPressed_givenKeyPressAndRelease_thenStateChangesToFalse() {
        // Add the key (simulate key press)
        keys.add(KeyCode.W);
        assertTrue(keys.isPressed(KeyCode.W), "Key W should be pressed after add()");

        // Remove the key (simulate key release)
        keys.remove(KeyCode.W);

        // Verify key is no longer pressed
        assertFalse(keys.isPressed(KeyCode.W), "Key W should not be pressed after remove()");
    }

    @Test
    public void isPressed_givenMultipleKeysPressed_thenCorrectStateForEachKey() {
        // Add multiple keys
        keys.add(KeyCode.A);
        keys.add(KeyCode.D);
        keys.add(KeyCode.W);

        // Verify all keys are pressed
        assertTrue(keys.isPressed(KeyCode.A), "Key A should be pressed");
        assertTrue(keys.isPressed(KeyCode.D), "Key D should be pressed");
        assertTrue(keys.isPressed(KeyCode.W), "Key W should be pressed");

        // Verify unPressed key returns false
        assertFalse(keys.isPressed(KeyCode.S), "Key S should not be pressed");
    }

    @Test
    public void isPressed_givenMultipleKeysWithSomeReleased_thenCorrectIndividualStates() {
        // Add multiple keys
        keys.add(KeyCode.LEFT);
        keys.add(KeyCode.RIGHT);
        keys.add(KeyCode.UP);
        keys.add(KeyCode.DOWN);

        // Release some keys
        keys.remove(KeyCode.LEFT);
        keys.remove(KeyCode.UP);

        // Verify correct states
        assertFalse(keys.isPressed(KeyCode.LEFT), "Key LEFT should not be pressed after removal");
        assertTrue(keys.isPressed(KeyCode.RIGHT), "Key RIGHT should still be pressed");
        assertFalse(keys.isPressed(KeyCode.UP), "Key UP should not be pressed after removal");
        assertTrue(keys.isPressed(KeyCode.DOWN), "Key DOWN should still be pressed");
    }

    @Test
    public void isPressed_givenKeyNotAddedBefore_thenReturnsFalse() {
        // Test default behavior for keys that were never added
        assertFalse(keys.isPressed(KeyCode.SPACE), "Untracked key should return false");
        assertFalse(keys.isPressed(KeyCode.ENTER), "Untracked key should return false");
        assertFalse(keys.isPressed(KeyCode.ESCAPE), "Untracked key should return false");
    }

    @Test
    public void add_givenSameKeyPressedMultipleTimes_thenStateRemainsTrue() {
        // Add the same key multiple times
        keys.add(KeyCode.SHIFT);
        assertTrue(keys.isPressed(KeyCode.SHIFT), "Key SHIFT should be pressed");

        keys.add(KeyCode.SHIFT);
        assertTrue(keys.isPressed(KeyCode.SHIFT), "Key SHIFT should still be pressed after multiple adds");

        keys.add(KeyCode.SHIFT);
        assertTrue(keys.isPressed(KeyCode.SHIFT), "Key SHIFT should still be pressed after third add");
    }

    @Test
    public void remove_givenSameKeyReleasedMultipleTimes_thenStateRemainsFalse() {
        // Add and then remove a key
        keys.add(KeyCode.S);
        keys.remove(KeyCode.S);
        assertFalse(keys.isPressed(KeyCode.S), "Key S should not be pressed after removal");

        // Remove the same key again
        keys.remove(KeyCode.S);
        assertFalse(keys.isPressed(KeyCode.S), "Key S should still not be pressed after multiple removals");
    }

    @Test
    public void keyHandling_givenComplexKeySequence_thenAllStatesCorrect() {
        // Simulate a complex sequence of key presses and releases
        // Player 1 controls
        keys.add(KeyCode.A);     // Move left
        keys.add(KeyCode.W);     // Jump

        assertTrue(keys.isPressed(KeyCode.A), "A should be pressed");
        assertTrue(keys.isPressed(KeyCode.W), "W should be pressed");
        assertFalse(keys.isPressed(KeyCode.D), "D should not be pressed");

        // Player 2 joins
        keys.add(KeyCode.LEFT);   // Player 2 move left
        keys.add(KeyCode.UP);     // Player 2 jump

        assertTrue(keys.isPressed(KeyCode.LEFT), "LEFT should be pressed");
        assertTrue(keys.isPressed(KeyCode.UP), "UP should be pressed");

        // Player 1 stops moving left but continues jumping
        keys.remove(KeyCode.A);

        assertFalse(keys.isPressed(KeyCode.A), "A should not be pressed after removal");
        assertTrue(keys.isPressed(KeyCode.W), "W should still be pressed");
        assertTrue(keys.isPressed(KeyCode.LEFT), "LEFT should still be pressed");
        assertTrue(keys.isPressed(KeyCode.UP), "UP should still be pressed");

        // Both players land (stop jumping)
        keys.remove(KeyCode.W);
        keys.remove(KeyCode.UP);

        assertFalse(keys.isPressed(KeyCode.W), "W should not be pressed");
        assertFalse(keys.isPressed(KeyCode.UP), "UP should not be pressed");
        assertTrue(keys.isPressed(KeyCode.LEFT), "LEFT should still be pressed");
    }
}
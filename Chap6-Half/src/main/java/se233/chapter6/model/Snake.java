package se233.chapter6.model;

import javafx.geometry.Point2D;
import se233.chapter6.view.GameStage;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private Direction direction;
    private Direction previousDirection;
    private Point2D head;
    private Point2D prev_tail;
    private List<Point2D> body;

    public Snake(Point2D position) {
        direction = Direction.DOWN;
        previousDirection = Direction.DOWN;
        body = new ArrayList<>();
        this.head = position;
        this.body.add(this.head);
    }

    public void move() {
        prev_tail = body.get(body.size() - 1);
        previousDirection = direction;
        head = head.add(direction.current);
        body.remove(body.size() - 1);
        body.add(0, head);
    }

    private boolean isOppositeDirection(Direction current, Direction newDirection) {
        return (current == Direction.UP && newDirection == Direction.DOWN) ||
                (current == Direction.DOWN && newDirection == Direction.UP) ||
                (current == Direction.LEFT && newDirection == Direction.RIGHT) ||
                (current == Direction.RIGHT && newDirection == Direction.LEFT);
    }

    public void setDirection(Direction newDirection) {
        if (isOppositeDirection(this.direction, newDirection)) {
            return;
        }

        this.previousDirection = this.direction;
        this.direction = newDirection;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Direction getPreviousDirection() {
        return this.previousDirection;
    }

    public Point2D getHead() {
        return head;
    }

    public boolean collided(Food food) {
        return head.equals(food.getPosition());
    }

    public void grow() {
        body.add(prev_tail);
    }

    public int getLength() {
        return body.size();
    }

    public List<Point2D> getBody() {
        return body;
    }

    public boolean checkDead() {
        // Check if snake is out of bounds
        if (head.getX() < 0 || head.getY() < 0 ||
                head.getX() >= GameStage.WIDTH || head.getY() >= GameStage.HEIGHT) {
            return true;
        }

        // Check if snake hit itself - exclude the head (index 0)
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }

        return false;
    }
}
package com.mygdx.game.Entity;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Screens.Fasaar;
import com.mygdx.game.Utils;

public abstract class Entity {
    public enum Direction {
        SOUTH, SOUTHWEST, WEST, NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST
    }
    public enum State {
        IDLE, WALKING, SPRINTING
    }

    public Vector2 position;
    public Direction direction;
    public State state;

    public Entity() {
        this(0, 0);
    }

    public Entity(float x, float y) {
        position = new Vector2(x, y);
        direction = Direction.NORTH;
        state = State.IDLE;
    }

    public Direction getDirectionToCamera() {
        float deltaX = Fasaar.scene.cam.position.x - position.x;
        float deltaY = Fasaar.scene.cam.position.z - position.y;
        Direction directionToCamera = Utils.vectorToDirection(deltaX, deltaY);
        Direction[] directions = Direction.values();
        int i = direction.ordinal() - directionToCamera.ordinal();
        if (i < 0) {
            i += 8;
        }
        return directions[i];
    }

    public String toString() {
        return position.toString() + "\n" + direction + "\n" + state;
    }
}

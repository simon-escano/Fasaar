package com.mygdx.game;

import com.mygdx.game.Entity.Entity;

public class Utils {
    public static Entity.Direction vectorToDirection(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(x, y));
        return angleToDirection(angle);
    }

    public static Entity.Direction angleToDirection(float angle) {
        if (angle < 0) {
            angle += 360;
        }

        if (angle >= 337.5 || angle < 22.5) {
            return Entity.Direction.EAST;
        } else if (angle >= 22.5 && angle < 67.5) {
            return Entity.Direction.NORTHEAST;
        } else if (angle >= 67.5 && angle < 112.5) {
            return Entity.Direction.NORTH;
        } else if (angle >= 112.5 && angle < 157.5) {
            return Entity.Direction.NORTHWEST;
        } else if (angle >= 157.5 && angle < 202.5) {
            return Entity.Direction.WEST;
        } else if (angle >= 202.5 && angle < 247.5) {
            return Entity.Direction.SOUTHWEST;
        } else if (angle >= 247.5 && angle < 292.5) {
            return Entity.Direction.SOUTH;
        } else if (angle >= 292.5 && angle < 337.5) {
            return Entity.Direction.SOUTHEAST;
        }
        return null;
    }
}

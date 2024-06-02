package com.mygdx.game.Entity.Leviathan;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Entity.Entity;
import com.mygdx.game.Utils;

public abstract class Leviathan extends Entity {
    public Integer targetID;
    public float velocity;
    public Leviathan() {
        super();
    }

    public Leviathan(float x, float y) {
        super(x, y);
    }
    public void chase(float x, float y) {
        if (targetID == null) {
            return;
        }
        Vector2 d = new Vector2(x, y).sub(position).nor();
        direction = Utils.vectorToDirection(d.x, d.y);
        if (position.x < x) {
            position.x += velocity;
        } else {
            position.x -= velocity;
        }

        if (position.y < y) {
            position.y += velocity;
        } else {
            position.y -= velocity;
        }
    }
}

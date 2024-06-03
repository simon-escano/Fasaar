package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Orb {
    public Vector2 position;
    public boolean isCollected;

    public Orb(float x, float y) {
        position = new Vector2(x, y);
        isCollected = false;
    }

    public void pickup() {
        isCollected = true;
    }
}
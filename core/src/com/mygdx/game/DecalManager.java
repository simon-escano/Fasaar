package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.mygdx.game.Entity.Astronaut;
import com.mygdx.game.Entity.Leviathan.Leviathan;
import com.mygdx.game.Screens.Fasaar;

import java.util.ArrayList;

public class DecalManager {
    private final DecalBatch batch;
    private final ArrayList<Sprite> sprites;

    public DecalManager(CameraGroupStrategy cameraGroupStrategy) {
        batch = new DecalBatch(cameraGroupStrategy);
        sprites = new ArrayList<>();
    }

    public void update() {
        for (Sprite sprite : sprites) {
            if (sprite.entity instanceof Astronaut) {
                if (((Astronaut) sprite.entity).id == Fasaar.id) {
                    continue;
                }
            }
            sprite.draw(batch);
        }
    }

    public void add(Sprite sprite) {
        sprites.add(sprite);
    }

    public void flush() {
        batch.flush();
    }

    public boolean contains(int id) {
        for (Sprite sprite : sprites) {
            if (sprite.entity instanceof Astronaut) {
                if (((Astronaut) sprite.entity).id == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsLeviathan() {
        for (Sprite sprite : sprites) {
            if (sprite.entity instanceof Leviathan) {
                return true;
            }
        }
        return false;
    }
}

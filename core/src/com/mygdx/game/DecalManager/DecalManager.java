package com.mygdx.game.DecalManager;

import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.mygdx.game.Entity.Astronaut;
import com.mygdx.game.Entity.Leviathan.Leviathan;
import com.mygdx.game.Screens.Fasaar;

import java.util.ArrayList;

public class DecalManager {
    private final DecalBatch batch;
    public final ArrayList<Sprite> sprites;

    public DecalManager(CameraGroupStrategy cameraGroupStrategy) {
        batch = new DecalBatch(cameraGroupStrategy);
        sprites = new ArrayList<>();
    }

    public void update() {
        for (Sprite sprite : sprites) {
            if (sprite.object instanceof Astronaut) {
                if (((Astronaut) sprite.object).id == Fasaar.id) {
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
            if (sprite.object instanceof Astronaut) {
                if (((Astronaut) sprite.object).id == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsLeviathan() {
        for (Sprite sprite : sprites) {
            if (sprite.object instanceof Leviathan) {
                return true;
            }
        }
        return false;
    }

    public void removeLeviathan() {
        for (int i = 0; i < sprites.size(); i++) {
            if (sprites.get(i).object instanceof Leviathan) {
                sprites.remove(i);
                break;
            }
        }
    }
}

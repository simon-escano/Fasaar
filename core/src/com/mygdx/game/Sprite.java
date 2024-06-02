package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Entity.Astronaut;
import com.mygdx.game.Entity.Entity;
import com.mygdx.game.Entity.Leviathan.Cthulhu;
import com.mygdx.game.Entity.Leviathan.ForestLurker;
import com.mygdx.game.Entity.Leviathan.Leviathan;
import com.mygdx.game.Entity.Leviathan.Python;
import com.mygdx.game.Screens.Fasaar;

import static com.mygdx.game.Screens.Fasaar.scene;

public class Sprite {
    public Entity entity;
    private final TextureRegion[][] sprites;
    public int size;
    private int rows = 0;
    private final int cols = 8;
    private int ctr = 0;
    private float stateTime = 0;

    public Sprite(Entity entity) {
        this.entity = entity;
        String path = "sprites/", pathExtras = "";
        if (entity instanceof Astronaut) {
            path += "astronauts/";
            pathExtras += ((Astronaut) entity).getColor();
            rows = 5;
            size = 80;
        } else if (entity instanceof Leviathan) {
            path += "leviathans/";
            if (entity instanceof Python) {
                rows = 3;
                size = 200;
            } else if (entity instanceof ForestLurker) {
                rows = 4;
                size = 100;
            } else if (entity instanceof Cthulhu) {
                rows = 1;
                size = 500;
            }
        }
        Texture spritesheet = new Texture(Gdx.files.internal(path + entity.getClass().getSimpleName().toLowerCase() + pathExtras + ".png"));
        sprites = TextureRegion.split(spritesheet, spritesheet.getWidth() / cols, spritesheet.getHeight() / rows);
    }

    public void draw(DecalBatch decalBatch) {
        float animationSpeed = 0.1f;
        if (entity.state == Entity.State.IDLE) {
            ctr = 0;
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
            if (entity.state == Entity.State.SPRINTING) {
                animationSpeed = 0.075f;
            }
            if (stateTime > animationSpeed) {
                ctr++;
                if (ctr > rows - 1) {
                    ctr = 1;
                }
                stateTime = 0;
            }
        }

        Decal decal = Decal.newDecal(size, size, sprites[ctr][entity.getDirectionToCamera().ordinal()], true);
        decal.setPosition(entity.position.x, Fasaar.getY(entity.position.x, entity.position.y, size), entity.position.y);
        faceCameraPerpendicularToGround(decal);
        applyLighting(decal);
        decalBatch.add(decal);
    }

    private void faceCameraPerpendicularToGround(Decal decal) {
        Vector3 cameraPosition = new Vector3(scene.cam.position);
        Vector3 decalPosition = new Vector3(decal.getPosition());
        Vector3 direction = cameraPosition.sub(decalPosition);
        direction.y = 0;
        direction.nor();
        Quaternion rotation = new Quaternion();
        rotation.setFromCross(Vector3.Z, direction);
        decal.setRotation(rotation);
    }

    private void applyLighting(Decal decal) {
        float renderDistance = 700f;
        float distance = scene.cam.position.dst(decal.getPosition());
        float darkness = 0.625f - (distance / renderDistance);
        darkness = Math.max(darkness, 0.1f);
        decal.setColor(new Color(darkness, darkness, darkness, 1f));
    }
}

package com.mygdx.game.DecalManager;

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
import com.mygdx.game.Orb;
import com.mygdx.game.Screens.Fasaar;

import static com.mygdx.game.Screens.Fasaar.scene;

public class Sprite {
    public Object object;
    private final TextureRegion[][] sprites;
    public int size;
    private int rows = 0;
    private final int cols = 8;
    private int ctr = 0;
    private float stateTime = 0;

    public Sprite(Object object) {
        this.object = object;
        String path = "sprites/", pathExtras = "";

        if (object instanceof Entity) {
            if (object instanceof Astronaut) {
                path += "astronauts/";
                pathExtras += ((Astronaut) object).getColor();
                rows = 5;
                size = 80;
            } else if (object instanceof Leviathan) {
                path += "leviathans/";
                if (object instanceof Python) {
                    rows = 3;
                    size = 200;
                } else if (object instanceof ForestLurker) {
                    rows = 4;
                    size = 100;
                } else if (object instanceof Cthulhu) {
                    rows = 1;
                    size = 500;
                }
            }
            Texture spritesheet = new Texture(Gdx.files.internal(path + object.getClass().getSimpleName().toLowerCase() + pathExtras + ".png"));
            sprites = TextureRegion.split(spritesheet, spritesheet.getWidth() / cols, spritesheet.getHeight() / rows);
        } else {
            sprites = null;
            size = 40;
        }
    }

    public void draw(DecalBatch decalBatch) {
        Decal decal = null;
        if (object != null) {
            if (object instanceof Entity) {
                float animationSpeed = 0.1f;
                if (((Entity) object).state == Entity.State.IDLE) {
                    ctr = 0;
                } else {
                    stateTime += Gdx.graphics.getDeltaTime();
                    if (((Entity) object).state == Entity.State.SPRINTING) {
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

                decal = Decal.newDecal(size, size, sprites[ctr][((Entity) object).getDirectionToCamera().ordinal()], true);
                decal.setPosition(((Entity) object).position.x, Fasaar.getY(((Entity) object).position.x, ((Entity) object).position.y, size), ((Entity) object).position.y);
            } else if (object instanceof Orb) {
                if (!((Orb) object).isCollected) {
                    decal = Decal.newDecal(size, size, new TextureRegion(new Texture("sprites/orb.png")), true);
                    decal.setPosition(((Orb) object).position.x, Fasaar.getY(((Orb) object).position.x, ((Orb) object).position.y), ((Orb) object).position.y);
                } else {
                    decal = null;
                }
            }
        }

        if (decal != null) {
            faceCameraPerpendicularToGround(decal);
            applyLighting(decal);
            decalBatch.add(decal);
        }
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

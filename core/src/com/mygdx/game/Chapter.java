package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.DecalManager.Sprite;
import com.mygdx.game.Screens.Fasaar;
import com.mygdx.game.Screens.Fin;

public class Chapter {
    public boolean inProgress;
    public String title;
    public Orb orb;
    Sound music;
    public Chapter(String title, float orbX, float orbZ) {
        inProgress = false;
        this.title = title;
        orb = new Orb(orbX, orbZ);
        Fasaar.decalManager.add(new Sprite(orb));
    }

    public Chapter(String title) {
        inProgress = false;
        this.title = title;
    }

    public void start() {
        inProgress = true;
        setMusic("sounds/chapter_3.mp3");
        music.play(0.5f);
    }

    public void end() {
        inProgress = false;
        music.stop();
        if (title.equals("Chapter 3: The Awakening")) {
            Fasaar.app.setScreen(new Fin(Fasaar.app));
        }
        if (Fasaar.currentChapter < Fasaar.chapters.size()) {
            Fasaar.client.sendTCP(Fasaar.currentChapter + 1);
        }
    }

    public void update() {
        if (!inProgress) {
            return;
        }
        if (orb != null) {
            if (orb.isCollected) {
                end();
            }
        }
    }

    public void setMusic(String filepath) {
        music = Gdx.audio.newSound(Gdx.files.internal(filepath));
    }
}

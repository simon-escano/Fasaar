package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;

public class Application extends Game {

    @Override
    public void create() {
        setScreen(new Login(this));
    }
}

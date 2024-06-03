package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Fin extends Application implements Screen {
    Application app;
    private Skin skin;
    private Stage stage;

    public Fin(Application app) {
        this.app = app;
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("UI/pixthulhu-ui.json"));

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Texture backgroundTexture = new Texture(Gdx.files.internal("UI/background.png"));
        Drawable backgroundDrawable = new TextureRegionDrawable(backgroundTexture);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.setBackground(backgroundDrawable);
        root.defaults().growX().space(10);
        root.pad(200);


        TextButton logoutButton = new TextButton("Go back", skin);
        root.add(logoutButton).width(logoutButton.getPrefWidth());

        root.row();

        TextButton exitButton = new TextButton("Go back", skin);
        root.add(logoutButton).width(logoutButton.getPrefWidth());

        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new Login(app));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void hide() {

    }
}

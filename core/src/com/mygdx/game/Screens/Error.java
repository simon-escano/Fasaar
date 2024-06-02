package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.lwjgl.opengl.GL20;

public class Error extends Application implements Screen {
    Application app;
    Screen previousScreen;
    String title;
    String subtitle;
    private Skin skin;
    private Stage stage;

    public Error(Application app, Screen previousScreen, String title, String subtitle) {
        this.app = app;
        this.previousScreen = previousScreen;
        this.title = title;
        this.subtitle = subtitle;
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

        Label titleLabel = new Label(title, skin, "title");
        titleLabel.setAlignment(Align.center);
        root.add(titleLabel).center();
        root.row();

        Label subtitleLabel = new Label(subtitle, skin, "subtitle");
        subtitleLabel.setAlignment(Align.center);
        root.add(subtitleLabel).padBottom(40);
        root.row();

        TextButton backButton = new TextButton("Go back", skin);
        root.add(backButton).width(backButton.getPrefWidth());

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(previousScreen);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.9f, .9f, .9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {

    }
}

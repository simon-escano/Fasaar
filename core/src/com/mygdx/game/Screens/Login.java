package com.mygdx.game.Screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Database.Account;
import org.lwjgl.opengl.GL20;

import java.sql.SQLException;
import java.util.ArrayList;

public class Login extends ApplicationAdapter implements Screen {
    private Application app;
    private Skin skin;
    private Stage stage;

    public Login(Application app) {
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

        Label title = new Label("FASAAR", skin, "title");
        root.add(title).padBottom(40);

        Label subtitle = new Label("Login", skin, "subtitle");
        root.add(subtitle).right().padBottom(40).width(subtitle.getPrefWidth());

        root.row();

        Label label = new Label("Username:", skin);
        root.add(label);
        root.row();

        TextField usernameField = new TextField("", skin);
        root.add(usernameField).colspan(2);
        root.row();

        label = new Label("Password:", skin);
        root.add(label);
        root.row();

        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);
        root.add(passwordField).colspan(2);
        root.row();

        TextButton loginButton = new TextButton("Login", skin);
        root.add(loginButton);

        TextButton registerButton = new TextButton("Register", skin);
        root.add(registerButton);

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    app.setScreen(new Error(app, Login.this, "Please fill all fields.", "Username and password cannot be empty"));
                    return;
                }

                ArrayList<String> res;
                try {
                    res = Account.findUser(username, password.hashCode());
                } catch (SQLException e) {
                    app.setScreen(new Error(app, Login.this, "Database error.", e.getMessage()));
                    return;
                }

                if (res == null) {
                    app.setScreen(new Error(app, Login.this, "Something went wrong.", "Please check your credentials."));
                    return;
                }
                int id = Integer.parseInt(res.get(0));
                app.setScreen(new Fasaar(app, id, res.get(1)));
            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new Register(app));
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
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {

    }
}

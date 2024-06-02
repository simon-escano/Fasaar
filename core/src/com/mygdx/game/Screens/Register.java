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

public class Register extends ApplicationAdapter implements Screen {
    private Application app;
    private Skin skin;
    private Stage stage;

    public Register(Application app) {
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

        Label subtitle = new Label("Register", skin, "subtitle");
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

        label = new Label("Email:", skin);
        root.add(label);
        root.row();

        TextField emailField = new TextField("", skin);
        root.add(emailField).colspan(2);
        root.row();

        label = new Label("Color: ", skin);
        root.add(label);
        root.row();

        SelectBox<String> selectBox = new SelectBox<>(skin);
        selectBox.setItems("Normal", "Pixel", "Red", "Yellow");
        root.add(selectBox).colspan(2);
        root.row();

        TextButton registerButton = new TextButton("Register", skin);
        root.add(registerButton);

        TextButton loginButton = new TextButton("Login", skin);
        root.add(loginButton);

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();
                String color = selectBox.getSelected();
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    app.setScreen(new Error(app, Register.this, "Please fill all fields.", "Every field should not be empty."));
                    return;
                }
                if (color.equals("normal")) {
                    color = "";
                } else {
                    color = "_" + color.toLowerCase();
                }
                try {
                    Account.createUser(username, password, email, color);
                    app.setScreen(new Login(app));
                } catch (SQLException e) {
                    app.setScreen(new Error(app, Register.this, "SQL Error", e.getMessage()));
                }
            }
        });

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.setScreen(new Login(app));
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

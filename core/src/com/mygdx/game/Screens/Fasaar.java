package com.mygdx.game.Screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.runtime.Mundus;
import com.mygdx.game.Chapter;
import com.mygdx.game.DecalManager.DecalManager;
import com.mygdx.game.DecalManager.Sprite;
import com.mygdx.game.Entity.Astronaut;
import com.mygdx.game.Entity.Entity;
import com.mygdx.game.Entity.Leviathan.Cthulhu;
import com.mygdx.game.Entity.Leviathan.ForestLurker;
import com.mygdx.game.Entity.Leviathan.Leviathan;
import com.mygdx.game.Entity.Leviathan.Python;
import com.mygdx.game.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class Fasaar extends ApplicationAdapter implements Screen {
	public static Application app;
	private Stage stage;
	Table root;
	public static Label chapterTitle;
	public static Label chapterInstructions;
	Label positionLabel;
	public static TextButton textButton;
	private Skin skin;
	public static int id;
	public static String color;
	public static Client client;
	private Mundus mundus;
	public static Scene scene;
	private static Terrain terrain;
	public static DecalManager decalManager;
	private Leviathan leviathan;
	private FirstPersonCameraController controller;
	private Astronaut player;
	private ArrayList<Astronaut> astronauts;
	public static ArrayList<Chapter> chapters;
	public static Integer currentChapter;
	private boolean escapeKeyPressed = false;

	public Fasaar(Application app, int id, String color) {
		this.app = app;
		Fasaar.id = id;
		Fasaar.color = color;
	}

	@Override
	public void create() {
		mundus = new Mundus(Gdx.files.internal("world"));
		scene = mundus.loadScene("Main Scene.mundus");
		terrain = mundus.getAssetManager().getTerrainAssets().get(0).getTerrain();
		decalManager = new DecalManager(new CameraGroupStrategy(scene.cam));

		chapters = new ArrayList<>();
		chapters.add(new Chapter("Prologue"));
		chapters.add(new Chapter("Chapter 1: Stranded", 670, 2000));
		chapters.add(new Chapter("Chapter 2: The Forest of Shadows", 1800, 2500));
		chapters.add(new Chapter("Chapter 2: The Ruins of The Forgotten", 1700, 1000));
		chapters.add(new Chapter("Chapter 3: The Awakening", 260, 230));

		if (scene.cam instanceof PerspectiveCamera) {
			((PerspectiveCamera) scene.cam).fieldOfView = 90;
		}

		controller = new FirstPersonCameraController(scene.cam);
		controller.setVelocity(100f);
		Gdx.input.setInputProcessor(controller);
		scene.cam.position.set(260, getY(260, 230), 230);
		player = new Astronaut(id, color);
		player.position.x = scene.cam.position.x;
		player.position.y = scene.cam.position.z;

		astronauts = new ArrayList<>();

		handleClient();
	}

	@Override
	public void dispose() {
		mundus.dispose();
	}

	@Override
	public void show() {
		create();
		stage = new Stage(new ScreenViewport());

		skin = new Skin(Gdx.files.internal("UI/pixthulhu-ui.json"));
		createHUD();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (currentChapter != null) {
			if (!chapters.get(currentChapter).inProgress) {
				chapters.get(currentChapter).start();
			}
			chapters.get(currentChapter).update();

			if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
				if (chapters.get(currentChapter).orb != null && player.position.dst(chapters.get(currentChapter).orb.position) < 50) {
					chapters.get(currentChapter).orb.pickup();
				}
			}
		}

		handleKeyInput();
		scene.cam.position.y = getY(scene.cam.position.x, scene.cam.position.z);
		player.position.set(scene.cam.position.x, scene.cam.position.z);
		player.direction = Utils.vectorToDirection(scene.cam.direction.x, scene.cam.direction.z);
		client.sendTCP(player);

		for (Astronaut astronaut : astronauts) {
			if (!decalManager.contains(astronaut.id)) {
				decalManager.add(new Sprite(astronaut));
			}
		}

		if (leviathan != null) {
			if (!decalManager.containsLeviathan()) {
				decalManager.add(new Sprite(leviathan));
			}
			if (leviathan.targetID.equals(id)) {
				leviathan.chase(player.position.x, player.position.y);
				client.sendTCP(leviathan);
			}
		}

		decalManager.update();
		controller.update();
		scene.sceneGraph.update();
		scene.render();

		decalManager.flush();

		positionLabel.setText("Position: " + (int) player.position.x + ", " + (int) scene.cam.position.y + ", " + (int) player.position.y);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		scene.cam.viewportWidth = width;
		scene.cam.viewportHeight = height;
		scene.cam.update();
	}

	@Override
	public void hide() {

	}

	public static float getY(float x, float z) {
		return terrain.getHeightAtWorldCoord(x, z, new Matrix4());
	}

	public static float getY(float x, float z, int height) {
		return getY(x, z) + ((((float) height / 2) - 58));
	}

	private void handleKeyInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
				player.state = Entity.State.SPRINTING;
				controller.setVelocity(150f);
			} else {
				player.state = Entity.State.WALKING;
				controller.setVelocity(100f);
			}
		} else {
			player.state = Entity.State.IDLE;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			escapeKeyPressed = !escapeKeyPressed;
			if (escapeKeyPressed) {
				Gdx.input.setInputProcessor(stage);
				root.clear();
				createEscapeMenu();
			} else {
				Gdx.input.setInputProcessor(controller);
				root.clear();
				createHUD();
			}
		}
	}

	private void updateAstronauts(Astronaut astronaut) {
		boolean updated = false;
		for (Astronaut a : astronauts) {
			if (a.id == astronaut.id) {
				a.position.set(astronaut.position);
				a.direction = astronaut.direction;
				a.state = astronaut.state;
				updated = true;
				break;
			}
		}

		if (!updated) {
			astronauts.add(astronaut);
		}
	}

	private void handleClient() {
		client = new Client();
		client.start();
		Kryo kryo = client.getKryo();
		kryo.register(Vector2.class);
		kryo.register(Entity.Direction.class);
		kryo.register(Entity.State.class);
		kryo.register(Astronaut.class);
		kryo.register(Python.class);
		kryo.register(ForestLurker.class);
		kryo.register(Cthulhu.class);

		try {
			client.connect(5000, "localhost", 54555, 54777);
		} catch (IOException e) {
			app.setScreen(new Error(app, Fasaar.this, "Unable to connect to server.", e.getMessage()));
        }

		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof Astronaut) {
					Astronaut astronaut = (Astronaut) object;
					updateAstronauts(astronaut);
				}

				if (object instanceof Leviathan) {
					if (leviathan == null) {
						leviathan = (Leviathan) object;
					} else {
						leviathan.targetID = ((Leviathan) object).targetID;
						leviathan.position.set(((Leviathan) object).position);
						leviathan.direction = ((Leviathan) object).direction;
						leviathan.state = ((Leviathan) object).state;
					}
				}

				if (object instanceof Integer) {
					currentChapter = (Integer) object;
					chapterTitle.setText(chapters.get(currentChapter).title);
					decalManager.removeLeviathan();
				}
			}
		});
    }

	private void createHUD() {
		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		root.defaults().space(10);
		root.pad(50);

		chapterTitle = new Label("", skin, "subtitle");
		root.add(chapterTitle).expand().top().left();

		chapterInstructions = new Label("Collect all the orbs", skin);
		root.add(chapterInstructions).expand().top().right();

		root.row();

		positionLabel = new Label("", skin);
		root.add(positionLabel).expand().bottom().left();

		textButton = new TextButton("Orbs collected: 0/4", skin);
		root.add(textButton).expand().bottom().right();
	}

	private void createEscapeMenu() {
		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		root.defaults().space(10);
		root.pad(50);

		TextButton logoutButton = new TextButton("Log Out", skin);
		root.add(logoutButton);

		root.row();

		TextButton exitButton = new TextButton("Exit Game", skin);
		root.add(exitButton);

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
}
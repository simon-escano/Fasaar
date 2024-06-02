package com.mygdx.game.Screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.runtime.Mundus;
import com.mygdx.game.DecalManager;
import com.mygdx.game.Entity.Astronaut;
import com.mygdx.game.Entity.Entity;
import com.mygdx.game.Entity.Leviathan.Leviathan;
import com.mygdx.game.Entity.Leviathan.Python;
import com.mygdx.game.Sprite;
import com.mygdx.game.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class Fasaar extends ApplicationAdapter implements Screen {
	Application app;
	public static int id;
	public static String color;
	public static Client client;
	private Mundus mundus;
	public static Scene scene;
	private static Terrain terrain;
	private DecalManager decalManager;
	private Leviathan leviathan;
	private FirstPersonCameraController controller;
	private Astronaut player;
	private ArrayList<Astronaut> astronauts;

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
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

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

	private static float getY(float x, float z) {
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
			}
		});
    }
}
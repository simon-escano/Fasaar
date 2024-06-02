package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Database.Account;
import com.mygdx.game.Entity.Astronaut;
import com.mygdx.game.Entity.Entity;
import com.mygdx.game.Entity.Leviathan.Leviathan;
import com.mygdx.game.Entity.Leviathan.Python;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private final Map<Integer, Astronaut> astronauts;
    private Leviathan leviathan;
    private long lastUpdateTime = System.currentTimeMillis();

    public GameServer() throws IOException {
        astronauts = new HashMap<>();
        leviathan = new Python(260, 230);
        Server server = new Server();
        server.start();
        server.bind(54555, 54777);

        Kryo kryo = server.getKryo();
        kryo.register(Vector2.class);
        kryo.register(Entity.Direction.class);
        kryo.register(Entity.State.class);
        kryo.register(Astronaut.class);
        kryo.register(Python.class);

        Thread account = new Thread(new Account());
        account.start();

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Astronaut) {
                    Astronaut astronaut = (Astronaut) object;
                    astronauts.put(astronaut.id, astronaut);

                    for (Astronaut a : astronauts.values()) {
                        server.sendToAllExceptTCP(connection.getID(), a);
                    }
                }

                if (object instanceof Leviathan) {
                    leviathan.targetID = ((Leviathan) object).targetID;
                    leviathan.position.set(((Leviathan) object).position);
                    leviathan.direction = ((Leviathan) object).direction;
                    leviathan.state = ((Leviathan) object).state;
                }
            }

            @Override
            public void idle(Connection connection) {
                float distance = 99999;
                Astronaut closestAstronaut = null;
                for (Astronaut astronaut : astronauts.values()) {
                    if (distance > leviathan.position.dst(astronaut.position)) {
                        distance = leviathan.position.dst(astronaut.position);
                        closestAstronaut = astronaut;
                    }
                }
                if (closestAstronaut != null) {
                    leviathan.targetID = closestAstronaut.id;
                    server.sendToAllTCP(leviathan);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                astronauts.remove(connection.getID());
            }
        });
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }
}

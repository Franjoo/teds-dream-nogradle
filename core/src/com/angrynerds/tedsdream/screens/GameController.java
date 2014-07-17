package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.Assets;
import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.camera.CameraHelper;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.errors.CreatureParsingException;
import com.angrynerds.tedsdream.errors.MapLayerParsingException;
import com.angrynerds.tedsdream.events.*;
import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.angrynerds.tedsdream.gameobjects.PlayerRemote;
import com.angrynerds.tedsdream.gameobjects.items.Item;
import com.angrynerds.tedsdream.gameobjects.skeletals.*;
import com.angrynerds.tedsdream.input.IGameInputController;
import com.angrynerds.tedsdream.input.KeyboardInput;
import com.angrynerds.tedsdream.input.RemoteInput;
import com.angrynerds.tedsdream.map.GameInitialization;
import com.angrynerds.tedsdream.map.Map;
import com.angrynerds.tedsdream.ui.ControllerUI;
import com.angrynerds.tedsdream.util.C;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Author: Franz Benthin
 */
public class GameController extends ScreenAdapter {

    private String mapname = Assets.MAP_FOREST;

    private GameInitialization gameInitialization;

    // projection
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private CameraHelper cameraHelper;

    // map
    private Map map;

    private HashMap<Integer, PlayerRemote> playersRemotes;

    // game object arrays
    private Array<Creature> enemies;
    private Array<Creature> players;
    private Array<Item> items;
    private Player player;

    // y-sorting
    private Array<GameObject> helperArray = new Array<>();
    private Comparator<GameObject> comparatorY;

    // multiplayer
    private GameClient client;
    private StringBuilder stringBuilder = new StringBuilder();
    private String clientNames = "";

    private ControllerUI ui;

    private final Controller game;
    private final boolean isMultiplayer;
    private final boolean isServer;

    public GameController(Controller game, boolean isMultiplayer, boolean isServer) {
        this.game = game;
        this.isMultiplayer = isMultiplayer;
        this.isServer = isServer;

        // projection
        batch = new SpriteBatch();
        camera = new OrthographicCamera(C.VIEWPORT_WIDTH, C.VIEWPORT_HEIGHT);

        try {
            map = new Map(camera, Assets.instance().get(mapname));
        } catch (MapLayerParsingException e) {
            e.printStackTrace();
        }

        playersRemotes = new HashMap<>();
        ui = new ControllerUI();

        // player input listener
        IGameInputController listener;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            listener = ui.getListener();
        } else {
            listener = new KeyboardInput();
        }

        // create Activities for AI
        Activities.create(this, camera);

        // create gameobject arrays
        players = new Array<>();
        enemies = new Array<>();
        items = new Array<>();

        // add player
        player = new Player(listener, this);
        players.add(player);

        // server game settings
        if (this.isMultiplayer) {
            client = new GameClient(this);
        }

        // create gameobjects
        if (this.isServer || !this.isMultiplayer) {
            try {
                gameInitialization = new GameInitialization(Assets.instance().get(mapname), game.difficulty);
                createObjects(gameInitialization);
            } catch (CreatureParsingException e) {
                e.printStackTrace();
            }
        }

        // camera
        cameraHelper = new CameraHelper();
        cameraHelper.applyTo(camera);
        cameraHelper.setTarget(player);

        // Y sorting comparator
        helperArray = new Array<>();
        comparatorY = (o1, o2) -> {
            if (o1.getY() < o2.getY()) return 1;
            else if (o1.getY() > o2.getY()) return -1;
            return 0;
        };
    }

    public void createObjects(GameInitialization init) {

        for (int i = 0; i < init.getSpawnArray().size; i++) {
            CreatureSpawn spawn = init.getSpawnArray().get(i);
            if (spawn != null) {
                CreatureSpawn s = spawn;

                // Spider
                if (s.getCreatureType().equals(Spider.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Spider(Assets.instance().get("spine/spinne/spinne.atlas"),
                            MathUtils.lerp(Spider.SCALE_MIN, Spider.SCALE_MAX, s.getScale()), s.getAp(), s.getHp(), s.getX(), s.getY()));
                }

                // Rabbit
                else if (s.getCreatureType().equals(Rabbit.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Rabbit(Assets.instance().get("spine/hase/hase.atlas"),
                            MathUtils.lerp(Rabbit.SCALE_MIN, Rabbit.SCALE_MAX, s.getScale()), s.getAp(), s.getHp(), s.getX(), s.getY()));
                }

                // Goblin
                else if (s.getCreatureType().equals(Goblin.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Goblin(Assets.instance().get("spine/goblin/goblin.atlas"),
                            MathUtils.lerp(Goblin.SCALE_MIN, Goblin.SCALE_MAX, s.getScale()), s.getAp(), s.getHp(), s.getX(), s.getY()));
                }

                // Wizzard
                else if (s.getCreatureType().equals(Wizzard.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Wizzard(Assets.instance().get("spine/goblin/goblin.atlas"),
                            MathUtils.lerp(Wizzard.SCALE_MIN, Wizzard.SCALE_MAX, s.getScale()), s.getAp(), s.getHp(), s.getX(), s.getY()));
                }


            }
        }
    }


    public void update(float delta) {


//        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.update(delta);

//        }

        player.update(delta);
        for (int i = 0; i <= playersRemotes.size(); i++) {
            if (playersRemotes.containsKey(i)) {
                playersRemotes.get(i).getPlayer().remoteUpdate(delta);
            }
        }

        map.update(delta);

        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).update(delta);
        }

        // camera
        camera.update();
        cameraHelper.update(delta);
        cameraHelper.applyTo(camera);

        // menu on player dead
        if (player.getHP() <= 0) {
            game.setScreen(game.mainMenu);
        }


        // send game relevant data
        if (isMultiplayer) {
            try {
                client.write(player.getUpdateEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zoom.apply(camera,delta);

    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(camera.combined);

        map.renderBackground();

        batch.begin();

        // draw gameobjects ordered
        helperArray.clear();
        helperArray.addAll(players);
        helperArray.addAll(enemies);
        helperArray.addAll(items);
        helperArray.sort(comparatorY);

        for (int i = 0; i < helperArray.size; i++) {
            GameObject o = helperArray.get(i);
            Creature ownPlayer = players.get(0);
            if (Math.abs(ownPlayer.getX() - o.getX()) <= Gdx.graphics.getWidth()) {
//                shadowRenderer.drawShadows(batch, o);
                o.draw(batch);
            }
        }

        batch.end();

        map.renderForeground();

//        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            ui.render(delta);
//        }

        handleDebug(delta);

    }

    private void handleDebug(float delta) {

    }

    HashMap<Integer, PlayerRemote> getPlayerRemotes() {
        return playersRemotes;
    }

    public Array<Creature> getPlayers() {
        return players;
    }

    public Array<Creature> getEnemies() {
        return enemies;
    }

    public Array<Item> getItems() {
        return items;
    }

    void addPlayer(final int id) {

        RemoteInput remoteInput = new RemoteInput();
        Player player = new Player(remoteInput, this);
        player.setID(id);

        playersRemotes.put(id, new PlayerRemote(player, remoteInput));

        buildClientNameString();
        players.add(player);
        System.out.println("player added: " + id);
    }

    public void connect(final String host, final int port) {
        client.connect(host, port);

        System.out.println("isServer: " + isServer);

    }

    private void buildClientNameString() {
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append("Player ").append(player.getID()).append(" ( ME ) \n");

        playersRemotes.forEach((integer, playerRemote) -> stringBuilder.append("Player ").append(integer).append("\n"));


        clientNames = stringBuilder.toString();
    }

    public String getClientNames() {
        return clientNames;
    }

    /**
     * is responsible for server communication,
     * provides some methods to send game relevant data
     */
    private class GameClient implements Runnable, Disposable {

        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        private Socket socket;

        private final GameController game;

        public GameClient(GameController game) {
            this.game = game;
        }

        public void connect(final String host, final int port) {
            SocketHints socketHints = new SocketHints();
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, socketHints);
            System.out.println("server [" + host + "] connected @" + port);

            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(this).start();
        }


        @Override
        public void run() {

            while (true) {

                try {

                    final Serializable event = (Serializable) ois.readObject();

                    // assign id
                    if (event instanceof AssignIDEvent) {
                        player.setID(((AssignIDEvent) event).id);
                    }

                    // add new player
                    else if (event instanceof AddPlayerEvent) {
                        Gdx.app.postRunnable(() -> game.addPlayer(((AddPlayerEvent) event).getId()));

                        if (isServer) {
                            try {
                                client.write(gameInitialization.getEnemyInitializationEvent());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    // update player
                    else if (event instanceof UpdatePlayerEvent) {
                        //((UpdatePlayerEvent) event).apply(game);
                        UpdatePlayerEvent e = (UpdatePlayerEvent) event;

                        Player player = game.getPlayerRemotes().get((e.getId())).getPlayer();
                        player.run(e);
                    }

                    // enemy initialization
                    else if (event instanceof GameInitializationEvent) {
                        System.out.println("INITIALIZATION RECEIVED");

                        System.out.println(((GameInitializationEvent) event).getSerialization());

                        Json json = new Json();
                        final GameInitialization initialization = json.fromJson(GameInitialization.class, ((GameInitializationEvent) event).getSerialization());
                        Gdx.app.postRunnable(() -> createObjects(initialization));

                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(Serializable obj) throws IOException {
            oos.reset();
            oos.writeObject(obj);
        }

        @Override
        public void dispose() {
            socket.dispose();
        }
    }


}

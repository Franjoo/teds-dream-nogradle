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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

    private Texture tex_gameover = Assets.instance().get("ui/ingame/dreamover.png");
    private float gameoverTimer;


    private GameInitialization gameInitialization;

    // projection
    private SpriteBatch batch;
    private Color batchColor;
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
            map = new Map(camera, (TiledMap) Assets.instance().get(mapname));

        } catch (MapLayerParsingException e) {
            e.printStackTrace();
        }

        batchColor = new Color(1, 1, 1, 1);

        playersRemotes = new HashMap<>();
        ui = new ControllerUI();

        // player input listener
        IGameInputController listener;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            listener = ui.getListener();
        } else {
            listener = new KeyboardInput();
//            listener = ui.getListener();
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
        ui.setPlayer(player);

        // server game settings
        if (this.isMultiplayer) {
            client = new GameClient(this);
        }

        // create gameobjects
        if (this.isServer || !this.isMultiplayer) {
            try {
                gameInitialization = new GameInitialization((TiledMap) Assets.instance().get(mapname), game.difficulty);
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
        comparatorY = new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                if (o1.getY() < o2.getY()) return 1;
                else if (o1.getY() > o2.getY()) return -1;
                return 0;
            }
        };
    }


    public void createObjects(GameInitialization init) {

        for (int i = 0; i < init.getSpawnArray().size; i++) {
            CreatureSpawn spawn = init.getSpawnArray().get(i);
            if (spawn != null) {
                CreatureSpawn s = spawn;

                // Spider
                if (s.getCreatureType().equals(Spider.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Spider((com.badlogic.gdx.graphics.g2d.TextureAtlas) Assets.instance().get("spine/spinne/spinne.atlas"),
                            MathUtils.lerp(Spider.SCALE_MIN, Spider.SCALE_MAX, s.getScale()), // scale
                            MathUtils.lerp(Spider.AP_MIN, Spider.AP_MAX, s.getScale()), // ap
                            MathUtils.lerp(Spider.HP_MIN, Spider.HP_MAX, s.getScale()), // hp
                            s.getX(), s.getY()));
                }

                // Rabbit
                else if (s.getCreatureType().equals(Rabbit.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Rabbit((com.badlogic.gdx.graphics.g2d.TextureAtlas) Assets.instance().get("spine/hase/hase.atlas"),
                            MathUtils.lerp(Rabbit.SCALE_MIN, Rabbit.SCALE_MAX, s.getScale()), // scale
                            MathUtils.lerp(Rabbit.AP_MIN, Rabbit.AP_MAX, s.getScale()), // ap
                            MathUtils.lerp(Rabbit.HP_MIN, Rabbit.HP_MAX, s.getScale()), // hp
                            s.getX(), s.getY()));
                }

                // Goblin
                else if (s.getCreatureType().equals(Goblin.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Goblin((com.badlogic.gdx.graphics.g2d.TextureAtlas) Assets.instance().get("spine/goblin/goblin.atlas"),
                            MathUtils.lerp(Goblin.SCALE_MIN, Goblin.SCALE_MAX, s.getScale()), // scale
                            MathUtils.lerp(Goblin.AP_MIN, Goblin.AP_MAX, s.getScale()), // ap
                            MathUtils.lerp(Goblin.HP_MIN, Goblin.HP_MAX, s.getScale()), // hp
                            s.getX(), s.getY()));
                }

                // Wizzard
                else if (s.getCreatureType().equals(Wizzard.class.getSimpleName().toLowerCase())) {
                    enemies.add(new Wizzard((com.badlogic.gdx.graphics.g2d.TextureAtlas) Assets.instance().get("spine/goblin/goblin.atlas"),
                            MathUtils.lerp(Wizzard.SCALE_MIN, Wizzard.SCALE_MAX, s.getScale()), // scale
                            MathUtils.lerp(Wizzard.AP_MIN, Wizzard.AP_MAX, s.getScale()), // ap
                            MathUtils.lerp(Wizzard.HP_MIN, Wizzard.HP_MAX, s.getScale()), // hp
                            s.getX(), s.getY()));
                }

                // Black Widdow
                else if (s.getCreatureType().equals("widdow")) {
                    enemies.add(new Widdow((com.badlogic.gdx.graphics.g2d.TextureAtlas) Assets.instance().get("spine/boss/spinne.atlas"),
                            MathUtils.lerp(Widdow.SCALE_MIN, Widdow.SCALE_MAX, s.getScale()), // scale
                            MathUtils.lerp(Widdow.AP_MIN, Widdow.AP_MAX, s.getScale()), // ap
                            MathUtils.lerp(Widdow.HP_MIN, Widdow.HP_MAX, s.getScale()), // hp
                            s.getX(), s.getY()));
                }
            }


        }
    }


    public void update(float delta) {

        ui.update(delta);

        player.update(delta);

        // update if not dead
        if (player.isDead()) {
            map.stopBackGroundSound();
            Color c = batchColor;
            batchColor.set(c.r - delta * 0.25f, c.g - delta * 0.25f, c.b - delta * 0.25f, 1);

            for (int i = 0; i < enemies.size; i++) {
                Creature e = enemies.get(i);
                enemies.get(i).setColor(batchColor.r, batchColor.g, batchColor.b, e.getColor().a - delta * 0.25f);
            }

        }


        for (int i = 0; i <= playersRemotes.size(); i++) {
            if (playersRemotes.containsKey(i)) {
                playersRemotes.get(i).getPlayer().remoteUpdate(delta);
            }
        }

        map.update(delta);

        for (int i = 0; i < enemies.size; i++) {
            Creature enemy = enemies.get(i);

            // update if not dead
            enemy.updateAnimations(delta);
            if (!enemy.isDead()) {
                enemy.updatePosition(delta);

                if (enemy.hasAttacked() && enemy.getSkeletonBounds().aabbIntersectsSkeleton(player.getSkeletonBounds())) {
                    player.setDamage(enemy.getAP());
                }
            }

            // fade out
            else {
                Color c = enemy.getColor();
                enemy.setColor(c.r, c.g, c.b, c.a - delta);

                if (c.a <= 0) enemies.removeValue(enemy, true);
            }


        }

        // camera
        camera.update();
        cameraHelper.update(delta);
        cameraHelper.applyTo(camera);

        // menu on player dead
        if (player.getHP() <= 0) {
//            game.setScreen(game.mainMenu);
            map.stopBackGroundSound();
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

        map.renderBackground(batchColor);

        batch.setColor(batchColor);
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
        batch.setColor(1, 1, 1, 1);

        map.renderForeground(batchColor);

        // fade out
        if (player.isDead()) {
            gameoverTimer += delta;
            if (gameoverTimer >= 5 && Gdx.input.isTouched()) {
                game.setScreen(game.mainMenu);
                gameoverTimer = 0;
            }

            batch.begin();
            batch.draw(tex_gameover, camera.position.x - tex_gameover.getWidth() / 2,
                    camera.position.y - tex_gameover.getHeight() / 2);
            batch.end();
        }


        ui.render(batch);

        handleDebug(delta);

    }

    private void handleDebug(float delta) {

    }

    public void playBackGroundSound() {
        map.playBackGroundsound();
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

        for (int i = 0; i < playersRemotes.size(); i++) {
//            stringBuilder.append("Player ").append(playersRemotes.get(i).getPlayer().getID()).append("\n");
            stringBuilder.append("Player ").append("bla").append("\n");
        }
//
//        playersRemotes.forEach(new BiConsumer<Integer, PlayerRemote>() {
//            @Override
//            public void accept(Integer integer, PlayerRemote playerRemote) {
//                stringBuilder.append("Player ").append(integer).append("\n");
//            }
//        });


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
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.addPlayer(((AddPlayerEvent) event).getId());
                            }
                        });

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
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                createObjects(initialization);
                            }
                        });

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

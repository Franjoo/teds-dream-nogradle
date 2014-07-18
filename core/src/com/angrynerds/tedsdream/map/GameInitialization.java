package com.angrynerds.tedsdream.map;

import com.angrynerds.tedsdream.errors.CreatureParsingException;
import com.angrynerds.tedsdream.events.CreatureSpawn;
import com.angrynerds.tedsdream.events.GameInitializationEvent;
import com.angrynerds.tedsdream.events.Spawn;
import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Author: Franz Benthin
 */
public class GameInitialization implements Serializable {

    Array<CreatureSpawn> spawnArray;

    public GameInitialization() {
    }

    public GameInitialization(TiledMap tiledMap, float difficulty) throws CreatureParsingException {

        spawnArray = new Array<>();

        MapLayers mapLayers = tiledMap.getLayers();


        for (int i = 0; i < mapLayers.getCount(); i++) {
            // contains objects
            if (mapLayers.get(i).getObjects().getCount() != 0) {
                MapObjects objects = mapLayers.get(i).getObjects();

                for (int j = 0; j < objects.getCount(); j++) {
                    MapProperties p = objects.get(j).getProperties();

//                    Iterator<String> it = p.getKeys();
//                    while (it.hasNext()){
//                        String s = it.next();
//                        System.out.println(s + "  -  " + p.get(s).toString());
//                    }

                    String type = p.get("type").toString().trim().toLowerCase();
                    String name = objects.get(j).getName().trim().toLowerCase();

                    if (!p.containsKey("count"))
                        throw new CreatureParsingException("creature object must contain property 'count'");
                    int count = Integer.parseInt(p.get("count").toString());

                    for (int c = 0; c < count; c++) {
                        // parse creatures
                        if (type.equals("creature")) {
                            String[] args;

                            float x, y;
                            x = Float.parseFloat(p.get("x").toString());
                            y = Float.parseFloat(p.get("y").toString());

                            // spawn area
                            if (count > 1 && !p.containsKey("width"))
                                throw new CreatureParsingException("creature spawn must contain property 'width'");
                            else {
                                x = MathUtils.random(x, x + Float.parseFloat(p.get("width").toString()) * Map.getProperties().tileWidth);
                                y = MathUtils.random(Map.getProperties().boundsY_min, Map.getProperties().boundsY_max);
                            }


                            float scale = getRandomFromTupel("scale", p);
                            float ap = getRandomFromTupel("ap", p);
                            float hp = getRandomFromTupel("hp", p);
//

                            spawnArray.add(new CreatureSpawn(name, x, y, scale, ap, hp));

//                        int count = Integer.parseInt(p.get("count").toString());

                        }
                    }


//                    // position and dimension of spawn rectangle
//                    float x = Float.parseFloat(p.get("x").toString());
//                    float y = Float.parseFloat(p.get("y").toString());
//                    float w = Float.parseFloat(p.get("width").toString()) * 64;
//                    float h = Float.parseFloat(p.get("height").toString()) * 64;
//                    Rectangle rectangle = new Rectangle(x, y, w, h);

//                    // number of enemies in spawn area calc
//                    int min = Integer.parseInt(p.get("min").toString());
//                    int max = Integer.parseInt(p.get("max").toString());
//                    int num = (int) (min + (Math.random() * (max - min)));
//
//                    // name (type) of enemy
//                    String name = p.get("name").toString();
//
//                    // path (boss)
//                    String path = name;
//                    if (p.containsKey("path")) path = p.get("path").toString();
//
//                    // skin of enemy
//                    String skin = null;
//                    if (p.containsKey("skin")) skin = p.get("skin").toString();
//
//                    // hp & ap
//                    float ap = 3;
//                    float hp = 100;
//                    if (p.containsKey("ap")) ap = Float.parseFloat(p.get("ap").toString());
//                    if (p.containsKey("hp")) hp = Float.parseFloat(p.get("hp").toString());
//
//                    // difficulty
//                    ap *= difficulty;
//                    hp *= difficulty;
//
//
//                    for (int k = 0; k < num; k++) {
//
//                        float scale = 0.2f;
//                        if (p.containsKey("scale")) {
//                            String[] s = p.get("scale").toString().split(" ");
//                            float scaleMin = Float.parseFloat(s[0]);
//                            float scaleMax = Float.parseFloat(s[1]);
//                            scale = scaleMin + ((float) (Math.random() * (scaleMax - scaleMin)));
//                        }
//
//                        //Enemy enemy = new Enemy(name, "spine/" + path + "/", skin, scale, ap, hp);
//
//                        float _x = (float) (rectangle.x + Math.random() * rectangle.getWidth());
//                        float _y = (float) (rectangle.y + Math.random() * rectangle.getHeight());
//
//                        _y = (float) (Math.random() * 384);
//
//                        if (!(_x > 0 )){ throw new RuntimeException("spawn out of bounds");}
////                        if (!(_y > 0 && _y < 6 * 64)){ throw new RuntimeException("spawn out of bounds: " + _y);}
//
//                        spawnArray.add(new Spawn(name, "spine/" + path + "/", skin, scale, ap, hp, _x, _y));
//                    }
//
//                }
                }
            }
        }


    }

    public float getRandomFromTupel(String key, MapProperties p) {
        float value;
        String[] args = (p.get(key).toString().trim().split(","));
        if (args.length == 1) value = Float.parseFloat(args[0]);
        else value = MathUtils.random(Float.parseFloat(args[0]), Float.parseFloat(args[1]));
        return value;
    }


    public GameInitializationEvent getEnemyInitializationEvent() {
        Json json = new Json();
        String serialization = json.toJson(this);
        System.out.println(json.prettyPrint(this));
        return new GameInitializationEvent(serialization);
    }

    public Array<CreatureSpawn> getSpawnArray() {
        return spawnArray;
    }


}

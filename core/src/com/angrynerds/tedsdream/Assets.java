package com.angrynerds.tedsdream;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.util.HashMap;

/**
 * Author: Franz Benthin
 */
public class Assets extends AssetManager{ // singleton

    private static Assets instance;

    // map path constants
    public static final String MAP_FOREST = "maps/forest.tmx";

    private static final String DIR_SPINE = "spine/";
    private static final String DIR_MAPS = "maps/";
    private static final String DIR_UI = "ui/";
    private static final String DIR_SOUNDS = "sounds/";

    private HashMap<String, ?> assets;



    private Assets() {

        TmxMapLoader mapLoader = new TmxMapLoader(new InternalFileHandleResolver());
        assets = new HashMap<>();

        // ui elements
        setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
        load(DIR_UI + "loading.pack", TextureAtlas.class);
        finishLoading(); // needed for loading screen

        // tiled maps
        setLoader(TiledMap.class, mapLoader);
        load(DIR_MAPS + "forest.tmx", TiledMap.class);

        // creatures
        setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));

        load(DIR_SPINE + "ted/ted.atlas", TextureAtlas.class);
        load(DIR_SPINE + "lise/lise.atlas", TextureAtlas.class);

        load(DIR_SPINE + "spinne/spinne.atlas", TextureAtlas.class);
        load(DIR_SPINE + "hase/hase.atlas", TextureAtlas.class);
        load(DIR_SPINE + "goblin/goblin.atlas", TextureAtlas.class);


        // sounds
        load(DIR_SOUNDS + "ingame/lightsaber.mp3", Sound.class);
        load(DIR_SOUNDS + "ingame/dash.wav", Sound.class);


        // intro spine
//
//        manager.load(dir_spine + "intro/Sequenz1_start/sequenz_1.atlas", TextureAtlas.class);
//
//        // creature spine
//        manager.load(dir_spine + "spinne/spinne.atlas", TextureAtlas.class);
//        manager.load("ui/loading.pack", TextureAtlas.class);
//        manager.finishLoading();


        //manager.finishLoading();

//        while (!manager.update()) {
//            System.out.println(manager.getProgress());
//        }


    }

    @Override
    public synchronized boolean update() {


        return super.update();
    }

    public static Assets instance() {
        // lazy creation
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    // eager creation
    public static void create() {
        instance = new Assets();
    }


}

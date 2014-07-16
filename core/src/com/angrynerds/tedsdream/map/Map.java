package com.angrynerds.tedsdream.map;

import com.angrynerds.tedsdream.Layer;
import com.angrynerds.tedsdream.errors.MapLayerParsingException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public class Map {

    private static Properties properties;

    Vector3 vec3 = new Vector3();

    private OrthogonalTiledMapRenderer renderer;
    private Array<Layer> layers_foreground = new Array<>();
    private Array<Layer> layers_background = new Array<>();

    private OrthographicCamera camera;
    private TiledMap tiledMap;

    public Map(OrthographicCamera camera, TiledMap tiledMap) throws MapLayerParsingException {
        this.camera = camera;
        this.tiledMap = tiledMap;

        properties = new Properties(tiledMap);

        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        renderer.setView(camera);

        createMapLayers();
    }

    private void createMapLayers() throws MapLayerParsingException {
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            MapLayer mapLayer = tiledMap.getLayers().get(i);
            // if is tileLayer
            if (mapLayer.getObjects().getCount() == 0) {
                MapProperties mapProperties = mapLayer.getProperties();
                String name = mapLayer.getName();

                if (name.startsWith("bg") || name.startsWith("fg")) {

                    String[] args;

                    // position
                    if (!mapProperties.containsKey("p"))
                        throw new MapLayerParsingException("Layer: " + name + " must contain key p with vec2");
                    args = mapProperties.get("p").toString().toLowerCase().trim().split(",");
                    float x = Float.parseFloat(args[0]);
                    float y = Float.parseFloat(args[1]);

                    // velocity
                    boolean moveable = mapProperties.containsKey("m");
                    if (!mapProperties.containsKey("v"))
                        throw new MapLayerParsingException("Layer: " + name + " must contain key v with vec2");
                    args = mapProperties.get("v").toString().toLowerCase().trim().split(",");
                    float vx = Float.parseFloat(args[0]);
                    float vy = Float.parseFloat(args[1]);


                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;
                    System.out.println(tileLayer.getName() + "  " + "x: " + x + "y: " + y);

                    // create layer
                    Layer layer;
                    if (!moveable) {
                        layer = new Layer(x, y, vx, vy, tileLayer);
                    } else {
                        float mX = Float.parseFloat(mapProperties.get("mx").toString());
                        float mY = Float.parseFloat(mapProperties.get("my").toString());
                        layer = new Layer(x, y, vx, vx, tileLayer, mX, mY);
                    }

                    // background layer
                    if (tileLayer.getName().startsWith("bg")) {
                        layers_background.add(layer);
                    }

                    // foreground layer
                    else if (tileLayer.getName().startsWith("fg")) {
                        layers_foreground.add(layer);
                    }
                }
            }
        }
    }

    public void update(float delta) {
        for (int i = 0; i < layers_background.size; i++) {
            layers_background.get(i).update(delta);
        }

        for (int i = 0; i < layers_foreground.size; i++) {
            layers_foreground.get(i).update(delta);
        }
    }

    public void renderForeground() {
        render(layers_foreground);
    }

    public void renderBackground() {
        render(layers_background);
    }

    private void render(Array<Layer> layers) {
        vec3.set(camera.position);

        renderer.getSpriteBatch().begin();

        for (int i = 0; i < layers.size; i++) {

            // apply parallax offset
            Layer l = layers.get(i);
            camera.position.x = camera.position.x * l.getVelocityX() + Gdx.graphics.getWidth() / 2;
            camera.position.y = camera.position.y * l.getVelocityY();
            camera.update();

            // apply view
            renderer.setView(camera);
            renderer.renderTileLayer(layers.get(i).getTiledMapTileLayer());

            // reset position
            camera.position.set(vec3);
            camera.update();

        }
        renderer.getSpriteBatch().end();


    }

    public static Properties getProperties() {
        return properties;
    }

    public static class Properties {

        // map properties
        public final int numTilesX;
        public final int numTilesY;
        public final int tileWidth;
        public final int tileHeight;
        public final int mapWidth;
        public final int mapHeight;

        public Properties(TiledMap tiledMap) {
            // parse map properties
            numTilesX = Integer.parseInt(tiledMap.getProperties().get("width").toString());
            numTilesY = Integer.parseInt(tiledMap.getProperties().get("height").toString());
            tileWidth = Integer.parseInt(tiledMap.getProperties().get("tilewidth").toString());
            tileHeight = Integer.parseInt(tiledMap.getProperties().get("tileheight").toString());
            mapWidth = numTilesX * tileWidth;
            mapHeight = numTilesY * tileHeight;
        }

    }

}

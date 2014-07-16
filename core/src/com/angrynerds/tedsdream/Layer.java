package com.angrynerds.tedsdream;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Author: Franz Benthin
 */
public class Layer extends AbstractLayer {

    private TiledMapTileLayer tl;
    private float scale;
    private boolean useTS;
    private boolean movable;

    private float mX;
    private float mY;


    public Layer(float x, float y, float vX, float vY, TiledMapTileLayer tl) {
        super(x, y, vX, vY);
        this.tl = tl;

        movable = false;
    }

    public Layer(float x, float y, float vX, float vY, TiledMapTileLayer tl, float mX, float mY) {
        super(x, y, vX, vY);
        this.tl = tl;

        this.mX = mX;
        this.mY = mY;

        movable = true;
    }

    @Override
    public void update(float deltaTime) {
        if (movable) {
            x += mX * deltaTime;
            y += mY * deltaTime;
        }
    }

    public TiledMapTileLayer getTiledMapTileLayer() {
        return tl;
    }

}
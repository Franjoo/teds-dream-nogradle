package com.angrynerds.tedsdream;

/**
 * Author: Franz Benthin
 */
public abstract class AbstractLayer {

    protected float x,y;
    protected float velocityX;
    protected float velocityY;

    public AbstractLayer(float x, float y, float velocityX, float velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelocityX() {
        return velocityX;
    }
    public float getVelocityY() {
        return velocityY;
    }

    public abstract void update(float deltaTime);

}

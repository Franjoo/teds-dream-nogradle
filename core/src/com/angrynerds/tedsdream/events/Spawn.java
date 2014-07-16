package com.angrynerds.tedsdream.events;

import com.angrynerds.tedsdream.gameobjects.GameObject;

import java.io.Serializable;

/**
 * Author: Franz Benthin
 */
public class Spawn implements Serializable {

    private Class<? extends GameObject> type;
    private float x, y;
    private String name;
    private String path;
    private String skin;
    private float scale;
    private float ap, hp;

    // standard constructor needed for serialization
    public Spawn(){}

    public Spawn(String name, String path, String skin, float scale, float ap, float hp, float _x, float _y) {
        this.name = name;
        this.path = path;
        this.skin = skin;
        this.scale = scale;
        this.ap = ap;
        this.hp = hp;
        this.x = _x;
        this.y = _y;
    }

    public Class<? extends GameObject> getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSkin() {
        return skin;
    }

    public float getScale() {
        return scale;
    }

    public float getAp() {
        return ap;
    }

    public float getHp() {
        return hp;
    }
}
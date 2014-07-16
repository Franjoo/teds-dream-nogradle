package com.angrynerds.tedsdream.events;

import java.io.Serializable;

/**
 * Author: Franz Benthin
 */
public class CreatureSpawn implements Serializable {

    public String creatureType;
    public float x, y;
    public float ap, hp;
    public float scale;

    // needed for deserialization
    public CreatureSpawn(){}

    public CreatureSpawn(String creatureType, float x, float y, float scale, float ap, float hp) {
        this.creatureType = creatureType;
        this.scale = scale;
        this.x = x;
        this.y = y;
        this.ap = ap;
        this.hp = hp;
    }

    public String getCreatureType() {
        return creatureType;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAp() {
        return ap;
    }

    public float getHp() {
        return hp;
    }

    public float getScale() {
        return scale;
    }
}

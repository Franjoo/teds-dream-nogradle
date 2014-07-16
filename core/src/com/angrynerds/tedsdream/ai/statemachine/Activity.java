package com.angrynerds.tedsdream.ai.statemachine;

import com.angrynerds.tedsdream.screens.GameController;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public abstract class Activity<T>{

    protected Array<Activity> neighbors;
    protected T actor;
    protected GameController game;

    public Activity(T actor, GameController game) {
        this.actor = actor;
        this.game = game;

        neighbors = new Array<>();
    }

    public Array<Activity> getNeighbors() {
        return neighbors;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public abstract boolean update(float delta);

    public abstract float getScore();

    public static Activity FindBestNextActivity(Array<Activity> neighbors) {
        if (neighbors.size != 0) {
            Activity bestNext = neighbors.get(0);
            for (int i = 0; i < neighbors.size; i++) {
                Activity current = neighbors.get(i);
                if (current.getScore() > bestNext.getScore()) {
                    bestNext = current;
                }
            }

            return bestNext;
        }

        return null;
    }

}

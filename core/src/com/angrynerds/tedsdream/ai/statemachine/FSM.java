package com.angrynerds.tedsdream.ai.statemachine;

import com.badlogic.gdx.utils.Array;

/**
 * Author: Franz Benthin
 */
public class FSM {

    public Activity currentActivity;
    public Array<Activity> activities;

    public FSM() {
        activities = new Array<>();
    }

    public FSM(Array<Activity> activities) {
        this.activities = activities;
        if (activities.size != 0) {
            currentActivity = activities.first();
        }
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public boolean update(float delta) {
        boolean completed = currentActivity.update(delta);
        if (completed) {
            setCurrentActivity(Activity.FindBestNextActivity(currentActivity.getNeighbors()));
        }
        return completed;
    }

}

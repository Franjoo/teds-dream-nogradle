package com.angrynerds.tedsdream.gameobjects.skeletals;

import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.ai.statemachine.Activity;
import com.angrynerds.tedsdream.ai.statemachine.FSM;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Author: Franz Benthin
 */
public class Widdow extends Spider {

    // CREATURE TYPE CONSTANTS
    public static final float SCALE_MAX = 0.15f;
    public static final float SCALE_MIN = 0.08f;
    public static final float HP_MAX = 200;
    public static final float HP_MIN = 100;
    public static final float AP_MAX = 8;
    public static final float AP_MIN = 4;

    private float scale; // relative to scale bounds

    public Widdow(TextureAtlas atlas, float scale, float ap, float hp, float x, float y) {
        super(atlas, scale, ap, hp, x, y);
    }

    private void createAI() {
        ai = new FSM();

        // activity graph
        Activity<Creature> ac_runToPlayer = new Activities.RunToPlayer(this);
        Activity<Creature> ac_attackPlayer = new Activities.FearfulAttackPlayer(this, getHP() * 0.3f);
//        Activity<Creature> ac_hide = new Activities.HideFromPlayer(this, getHP() * 0.3f);
        Activity<Creature> ac_waitForPlayer = new Activities.WaitForPlayer(this);


        ac_waitForPlayer.getNeighbors().add(ac_runToPlayer);
        ac_runToPlayer.getNeighbors().add(ac_attackPlayer);
        ac_attackPlayer.getNeighbors().add(ac_runToPlayer);

        ai.setCurrentActivity(ac_waitForPlayer);
    }

    private void mixAnimations() {
        stateData.setMix("move", "attack", 0.2f);
        stateData.setMix("attack", "move", 0.2f);
        stateData.setMix("attack", "die", 0.5f);
        stateData.setMix("move", "die", 0.2f);
    }

    @Override
    public float getSpeed(Move speed) {
        switch (speed) {
            case WALK:
                return 100;
            case RUN:
                return 120;
            case HIDE:
                return 30;

        }

        return 0;
    }
}

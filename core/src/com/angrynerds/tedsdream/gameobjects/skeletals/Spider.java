package com.angrynerds.tedsdream.gameobjects.skeletals;

import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.ai.statemachine.Activity;
import com.angrynerds.tedsdream.ai.statemachine.FSM;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

/**
 * Author: Franz Benthin
 */
public class Spider extends Creature {

    // CREATURE TYPE CONSTANTS
    public static final float SCALE_MAX = 0.15f;
    public static final float SCALE_MIN = 0.08f;
    public static final float HP_MAX = 200;
    public static final float HP_MIN = 100;
    public static final float AP_MAX = 8;
    public static final float AP_MIN = 4;

    private float scale; // relative to scale bounds

    public Spider(TextureAtlas atlas, float scale, float ap, float hp, float x, float y) {
        super(atlas, "spine/spinne/spinne", scale, null, ap, hp, x, y);

        this.scale = scale / SCALE_MAX;

        createAI();

        mixAnimations();
    }


    public static Spider create(TextureAtlas atlas) {

        float size = (float) Math.random();
        float scale = (SCALE_MIN + size * (SCALE_MAX - SCALE_MIN));
        float hp = HP_MIN + (HP_MAX - HP_MIN) * size;
        float ap = AP_MIN + (AP_MAX - AP_MIN) * size;

        return new Spider(atlas, scale, ap, hp, 0, 0);
    }

    private void createAI() {
        ai = new FSM();

        // activity graph
        Activity<Creature> ac_runToPlayer = new Activities.RunToPlayer(this);
        Activity<Creature> ac_attackPlayer = new Activities.FearfulAttackPlayer(this, getHP() * 0.3f);
        Activity<Creature> ac_hide = new Activities.HideFromPlayer(this, getHP() * 0.3f);
        Activity<Creature> ac_waitForPlayer = new Activities.WaitForPlayer(this);


        ac_waitForPlayer.getNeighbors().add(ac_runToPlayer);
        ac_runToPlayer.getNeighbors().add(ac_attackPlayer);
        ac_runToPlayer.getNeighbors().add(ac_hide);
        ac_attackPlayer.getNeighbors().add(ac_runToPlayer);
        ac_attackPlayer.getNeighbors().add(ac_hide);

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
                return MathUtils.lerp(120, 60, scale);
            case RUN:
                return MathUtils.lerp(400, 100, scale);
            case HIDE:
                return MathUtils.lerp(240, 120, scale);

        }

        return 0;
    }

    public float getScale() {
        return scale;
    }
}

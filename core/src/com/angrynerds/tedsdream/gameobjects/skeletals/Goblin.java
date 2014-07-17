package com.angrynerds.tedsdream.gameobjects.skeletals;

import com.angrynerds.tedsdream.Assets;
import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.ai.statemachine.Activity;
import com.angrynerds.tedsdream.ai.statemachine.FSM;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

/**
 * Author: Franz Benthin
 */
public class Goblin extends Creature {

    // CREATURE TYPE CONSTANTS
    public static final float SCALE_MAX = 0.15f;
    public static final float SCALE_MIN = 0.08f;
    public static final float HP_MAX = 120;
    public static final float HP_MIN = 60;
    public static final float AP_MAX = 10;
    public static final float AP_MIN = 5;

    private Sound sound;

    private float scale; // relative to scale bounds


    public Goblin(TextureAtlas atlas, float scale, float ap, float hp, float x, float y) {
        super(atlas, "spine/goblin/goblin", scale, "goblin", ap, hp, x, y);

        this.scale = scale / SCALE_MAX;

        createAI();

        mixAnimations();

    }

    public static Goblin create(TextureAtlas atlas) {

        float size = (float) Math.random();
        float scale = (SCALE_MIN + size * (SCALE_MAX - SCALE_MIN));
        float hp = HP_MIN + (HP_MAX - HP_MIN) * size;
        float ap = AP_MIN + (AP_MAX - AP_MIN) * size;

        return new Goblin(atlas, scale, ap, hp, 0, 0);
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
    public void update(float deltaTime) {
        super.update(deltaTime);

        ai.update(deltaTime);
    }
    @Override
    public void playSound(String name){

//        sound = Assets.instance().get("sounds/ingame/creatures/goblin/" + name + ".mp3");
//        sound.play();


    }

    @Override
    public float getSpeed(Move speed) {
        return super.getSpeed(speed);
    }

    public float getScale() {
        return scale;
    }
}

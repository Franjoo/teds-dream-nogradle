package com.angrynerds.tedsdream.gameobjects.skeletals;

import com.angrynerds.tedsdream.ai.statemachine.Activities;
import com.angrynerds.tedsdream.ai.statemachine.Activity;
import com.angrynerds.tedsdream.ai.statemachine.FSM;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Author: Franz Benthin
 */
public class Partisan extends Creature {

    private FSM ai;

    public Partisan(TextureAtlas atlas, String path, float ap, float hp) {
        super(atlas, path, ap, hp);
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
    public float getSpeed(Move speed) {
        return super.getSpeed(speed);
    }

    public float getScale() {
        return scale;
    }
}

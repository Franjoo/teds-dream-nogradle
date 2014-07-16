package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.gameobjects.skeletals.Skeletal;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.AnimationState;

/**
 * Author: Franz Benthin
 */
public class IntroScreen implements Screen {

    private SpriteBatch batch;


    // ende
    private Skeletal animation;
    private AnimationState animationState;

    private Controller controller;

    public IntroScreen(Controller controller) {
        this.controller = controller;
//
//        batch = new SpriteBatch();
//
//        animation = new Skeletal(Assets.instance().getAtlas("sequenz_1"), "sequenz_1", 1.5f);
//        AnimationStateData animationStateData = new AnimationStateData(animation.getSkeletonData());
//        animationState = new AnimationState(animationStateData);
//        animationState.setAnimation(0, "start", false);
//
//        // center position
//        animation.getSkeleton().setX((Gdx.graphics.getWidth() / 2));
//        animation.getSkeleton().setY(-100);

    }

    @Override
    public void render(float delta) {
//        // update state
//        animation.getSkeleton().updateWorldTransform();
//        animation.getSkeleton().update(delta);
//        // animation.skeletonBounds.update(skeleton, true);
//
//        if (animationState.getCurrent(0).isComplete()) {
//            controller.setScreen(controller.nextScreen);
//        }
//
//
//        animationState.update(delta);
//        animationState.apply(animation.getSkeleton());
//
//        batch.begin();
//        animation.draw(batch);
//        batch.end();

                    controller.setScreen(controller.nextScreen);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    }
}

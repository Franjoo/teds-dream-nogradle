package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.Assets;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.gameobjects.skeletals.Skeletal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;

/**
 * Author: Franz Benthin
 */
public class IntroScreen extends AbstractScreen {

    private SpriteBatch batch;


    // ende
    private TextureAtlas atlas;

    private int screen = 0;

    private Skeletal skeleton;
    private Skeletal animation2;
    private Skeletal animation3;

    private AnimationState animationState;
    private AnimationState animationState2;
    private AnimationState animationState3;

    private Controller controller;


    public IntroScreen(Controller controller) {
        this.controller = controller;

        batch = new SpriteBatch();

        skeleton = new Skeletal((TextureAtlas) Assets.instance().get("sequenzes/intro/Sequenz1_start/sequenz_1.atlas"),"sequenzes/intro/Sequenz1_start/sequenz_1",1.2f);
        animation2 = new Skeletal((TextureAtlas) Assets.instance().get("sequenzes/intro/Sequenz2_mittel/sequenz_2.atlas"),"sequenzes/intro/Sequenz2_mittel/sequenz_2",2.7f);
        animation3 = new Skeletal((TextureAtlas) Assets.instance().get("sequenzes/intro/Sequenz3_ende/sequenz_3.atlas"),"sequenzes/intro/Sequenz3_ende/sequenz_3",1.5f);

        AnimationStateData animationStateData1 = new AnimationStateData(skeleton.getSkeletonData());
        AnimationStateData animationStateData2 = new AnimationStateData(animation2.getSkeletonData());
        AnimationStateData animationStateData3 = new AnimationStateData(animation3.getSkeletonData());

        animationState = new AnimationState(animationStateData1);
        animationState.setAnimation(0, "start", false);

        animationState2 = new AnimationState(animationStateData2);
        animationState2.setAnimation(0,"mittel",false);
        animationState3 = new AnimationState(animationStateData3);
        animationState3.setAnimation(0,"ende",false);

        // center position
        skeleton.getSkeleton().setX((Gdx.graphics.getWidth() / 2)+30);
        skeleton.getSkeleton().setY(0);
    }

    @Override
    public void render(float delta) {

        if (animationState.getCurrent(0).isComplete() || Gdx.input.justTouched()) {
            setNextScreen();
        }
        // update state
        skeleton.getSkeleton().updateWorldTransform();
        skeleton.getSkeleton().update(delta);
        // animation.skeletonBounds.update(skeleton, true);




        animationState.update(delta);
        animationState.apply(skeleton.getSkeleton());

        batch.begin();
        skeleton.draw(batch);
        batch.end();



    }

    private void setNextScreen(){
        if(screen == 0){
            animationState = animationState2;
            skeleton = animation2;
            skeleton.getSkeleton().setX(Gdx.graphics.getWidth()/2 -60);
            skeleton.getSkeleton().setY(0);
            screen++;

        }
       else if(screen == 1){
            animationState = animationState3;
            skeleton = animation3;
            skeleton.getSkeleton().setX(Gdx.graphics.getWidth()/2);
            skeleton.getSkeleton().setY(-75);
            screen++;
        }
        else {
            controller.setScreen(controller.playScreen);
            controller.playScreen.playBackGroundSound();
            screen = 0;
        }

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

    @Override
    public void update(float deltaTime) {

    }
}

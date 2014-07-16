package com.angrynerds.tedsdream.core;

import com.angrynerds.tedsdream.Assets;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

/**
 * Main class which provides the render() method to realize the game loop.
 * Through the implementation of the ApplicationListener Interface, this class
 * follows the standard Android activity life-cycle.
 */
public class Main implements ApplicationListener {

    // ENABLE LOGGING
    private static final boolean FPS_LOGGING = true;

    private Controller controller;
    private FPSLogger fpsLogger;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        Assets.create();

        while(!(Assets.instance().getProgress() <= 1)){
            System.out.println("progress: " + Assets.instance().getProgress());
        }

        controller = new Controller();
        fpsLogger = new FPSLogger();

    }

    @Override
    public void render() {

        // background color
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update - render
        controller.render();

        // log fps
        if (FPS_LOGGING) fpsLogger.log();
    }

    @Override
    public void resize(int width, int height) {
        controller.resize(width, height);
    }

    @Override
    public void pause() {
        controller.pause();
    }

    @Override
    public void resume() {
        controller.resume();
    }

    @Override
    public void dispose() {
        controller.dispose();
    }

}

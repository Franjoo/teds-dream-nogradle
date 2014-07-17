package com.angrynerds.tedsdream.screens;

import com.angrynerds.tedsdream.core.Controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 17.07.14
 * Time: 21:55
 * To change this template use File | Settings | File Templates.
 */
public class HelpScreen  implements Screen, GestureDetector.GestureListener {

    private Stage stage;
    private SpriteBatch batch;
    private int page = 0;
    private Texture[] helpPages = {new Texture("ui/menus/help/how-to-1.jpg"), new Texture("ui/menus/help/how-to-2.jpg"), new Texture("ui/menus/help/how-to-3.jpg")};

    private final Controller game;


    public HelpScreen(final Controller game) {
        this.game = game;
        stage = new Stage();
        batch = new SpriteBatch();
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(helpPages[page], 0, 0, stage.getWidth(), stage.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
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
        stage.dispose();
    }

    public void update(float deltaTime) {

    }

    @Override
    public boolean touchDown(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean tap(float v, float v2, int i, int i2) {
        if(++page >= helpPages.length) {
            game.setScreen(game.mainMenu);
        }
        return true;
    }

    @Override
    public boolean longPress(float v, float v2) {
        return false;
    }

    @Override
    public boolean fling(float v, float v2, int i) {
        return false;
    }

    @Override
    public boolean pan(float v, float v2, float v3, float v4) {
        return false;
    }

    @Override
    public boolean panStop(float v, float v2, int i, int i2) {
        return false;
    }

    @Override
    public boolean zoom(float v, float v2) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
        return false;
    }
}

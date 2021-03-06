package com.angrynerds.tedsdream.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import com.angrynerds.tedsdream.Assets;
import com.angrynerds.tedsdream.core.Controller;
import com.angrynerds.tedsdream.tweens.MenuAccessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 12.01.14
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class MainMenu implements Screen, TweenAccessor<Sound> {


    private Stage stage;
    private SpriteBatch batch;
    private Texture bg;
    private Skin skin;
    private Table table;
    private Button buttonPlay, buttonSettings, buttonHelp;
    private BitmapFont white;
    private TextureAtlas atlas;
    private Label heading;
    private TweenManager manager;

    // sounds
    private static final int SOUND_TITLE = 0;
    private float volume;
    private Sound sound_title;
    private long soundId;

    private InputListener listener;

    private final Controller game;


    public MainMenu(final Controller game) {
        this.game = game;

        // sound
        sound_title = Assets.instance().get("sounds/menus/titelmusik.wav");
        sound_title.setLooping(0, true);
        sound_title.setVolume(100, 0);

        stage = new Stage();
        atlas = new TextureAtlas("ui/menus/main/mainMenuButton.pack");
        skin = new Skin(atlas);
        white = new BitmapFont(Gdx.files.internal("fonts/bmtFont.fnt"), false);
        batch = new SpriteBatch();
        bg = new Texture("ui/menus/main/titel_moon_02.jpg");

        // table
        table = new Table(skin);
        table.setBounds(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2.0f, Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() / 6);

        // textbutton style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = white;

        // buttons
        buttonPlay = new Button(skin.getDrawable("button_play"));
        buttonSettings = new Button((skin.getDrawable("button_settings")));
        Skin helpButtonSkin = new Skin();
        helpButtonSkin.add("button", new Texture("ui/menus/main/questionMark.png"));
        buttonHelp = new Button(helpButtonSkin.getDrawable("button"));
        buttonHelp.setPosition(stage.getWidth() - buttonHelp.getWidth() - 5, 5);

        // add to table
        table.add(buttonPlay);
        table.add(buttonSettings);

        // align
        table.getCell(buttonPlay).size(buttonPlay.getWidth() / 2.5f, buttonPlay.getHeight() / 2.5f);
        table.getCell(buttonSettings).size(buttonSettings.getWidth() / 2.5f, buttonSettings.getHeight() / 2.5f);
        buttonPlay.padLeft(100);


        // configure tweens
        Tween.registerAccessor(buttonPlay.getClass(), new MenuAccessor());

        manager = new TweenManager();
        Tween.to(buttonPlay, MenuAccessor.POSITION_Y, 2.0f)
                .targetRelative(15)
                .ease(Sine.IN)
                .repeatYoyo(-1, 0)
                .start(manager);
        Tween.to(buttonSettings, MenuAccessor.POSITION_Y, 1.5f)
                .targetRelative(15)
                .ease(Sine.IN)
                .repeatYoyo(-1, 0)
                .start(manager);

        // create listener
        listener = new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                // button play
                if (event.getTarget() == buttonPlay) {
                    game.playScreen = new GameController(game, false, false);
                    game.nextScreen = game.playScreen;
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new GameController(game,false,false));
//                            game.setScreen(new IntroScreen(game));
                            System.out.println("play pressed");
                        }
                    });

                }

                // button settings
                else if (event.getTarget() == buttonSettings) {
                    game.setScreen(game.multiplayer_choose);
                    System.out.println("settings pressed (multiplayer)");
                } else if (event.getTarget() == buttonHelp) {
                    game.setScreen(new HelpScreen(game));
                }

                return true;
            }

        };

        buttonPlay.addListener(listener);
        buttonSettings.addListener(listener);
        buttonHelp.addListener(listener);

        // add to stage
        stage.addActor(table);
        stage.addActor(buttonHelp);
        table.debug();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Table.drawDebug(stage);
        manager.update(delta);

        batch.begin();
        batch.draw(bg, 0, 0, stage.getWidth(), stage.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
      sound_title.setVolume(0,1);
      soundId = sound_title.play();
        // play title music
//        sound_title.play(volume);

        //todo sound ausfaden, problem: kein volume getter um TweenAccessor zu benutzen

        // add listener
        buttonPlay.addListener(listener);
        buttonSettings.addListener(listener);
    }


    @Override
    public void hide() {
        sound_title.setVolume(0,1f);

        SoundFader s = new SoundFader(soundId);
        s.start();

        //sound_title.pause();
        // remove listener
        buttonPlay.removeListener(listener);
        buttonSettings.removeListener(listener);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        sound_title.dispose();
        bg.dispose();
        stage.dispose();
    }

    public void update(float deltaTime) {

    }

    @Override
    public int getValues(Sound target, int tweenType, float[] returnValues) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValues(Sound target, int tweenType, float[] newValues) {
        //To change body of implemented methods use File | Settings | File Templates.
            }

    class SoundFader extends Thread {

          long id;
        public SoundFader(long id){
             this.id=id;
        }
        @Override
        public void run() {
            for (float i = 1; i > 0 ; i-=0.002f) {

              sound_title.setVolume(id, i );

                try {
                    this.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            sound_title.stop();


        }
    }


}

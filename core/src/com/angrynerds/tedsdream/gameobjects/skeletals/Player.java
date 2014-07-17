package com.angrynerds.tedsdream.gameobjects.skeletals;

import com.angrynerds.tedsdream.Assets;
import com.angrynerds.tedsdream.events.UpdatePlayerEvent;
import com.angrynerds.tedsdream.gameobjects.items.HealthPotion;
import com.angrynerds.tedsdream.gameobjects.items.Item;
import com.angrynerds.tedsdream.input.IGameInputController;
import com.angrynerds.tedsdream.screens.GameController;
import com.angrynerds.tedsdream.util.States;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;

/**
 * Author: Franz Benthin
 */
public class Player extends Creature {

    // player constants
    public static final float HP_MAX = 300;
    public static final float AP_MAX = 15;

    // movement
    private float z;
    private float velocityX;
    private float velocityY;
    private float velocityX_MAX = 320;
    private float velocityY_MAX = 220;
    private float shadowHeight;
    private float shadowWidth;

    // stats
    private float maxHP = 100;
    private float actHP;
    private float atckDmg = 4;

    // helper attributes
    private boolean flipped;

    // animation
    private AnimationState state;
    private Array<String> attackAnimations;

    // sound_sword
    private Sound sound_sword = Assets.instance().get("sounds/ingame/lightsaber.mp3");
    private Sound sound_dash = Assets.instance().get("sounds/ingame/dash.wav");
    private Sound jump = Assets.instance().get("sounds/ingame/ted/jump_01.mp3");
    private Sound jump2 = Assets.instance().get("sounds/ingame/ted/jump_02.mp3");
    private Sound sound_walk = Assets.instance().get("sounds/ingame/ted/walk.wav");


    private ParticleEffect runningParticles = new ParticleEffect();

    private boolean dashRight;
    private boolean alive = true;


    // input
    private IGameInputController input;
    private GameController game;


    // multiplayer
    private UpdatePlayerEvent updateEvent;

    private int id = -1;

    public Player(IGameInputController input, GameController game) {
        super((TextureAtlas) Assets.instance().get("spine/lise/lise.atlas"), "spine/lise/lise", 0.20f, AP_MAX, HP_MAX);
//        super(Assets.instance().get("spine/ted/ted.atlas"), "spine/ted/ted", 0.20f, AP_MAX, HP_MAX);
        this.input = input;
        this.game = game;

        init();
    }

    public void init() {
        x = 500;
        y = 150;
        actHP = maxHP;

        setAnimationStates();

        setCurrentState();

        runningParticles.load(Gdx.files.internal("particles/walkingDust.p"), Gdx.files.internal("particles"));

        updateEvent = new UpdatePlayerEvent();
    }

    private void setAnimationStates() {
        attackAnimations = new Array<>();
        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

        for (int i = 0; i < stateData.getSkeletonData().getAnimations().size; i++) {
            String from = stateData.getSkeletonData().getAnimations().get(i).getName();
            if (from.startsWith("attack")) attackAnimations.add(from);
            for (int j = 0; j < stateData.getSkeletonData().getAnimations().size; j++) {
                String to = stateData.getSkeletonData().getAnimations().get(i).getName();

                if (!from.equals(to)) stateData.setMix(from, to, 0.4f);
            }
        }
//
//        stateData.setMix("move", "dash", 0.4f);
//        stateData.setMix("move", "attack_1", 0.4f);
//
//        stateData.setMix("attack_1", "move", 0.4f);
//        stateData.setMix("dash", "move", 0.4f);
//        stateData.setMix("move", "dash", 0.4f);
//        stateData.setMix("move", "die", 0.4f);
//        stateData.setMix("attack_1", "die", 0.4f);
//        stateData.setMix("jump", "die", 0.4f);
//        stateData.setMix("dash", "die", 0.4f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
        state.setAnimation(0, "move", true);

    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        runningParticles.draw(batch);
    }

    public String getAnimation() {
        return state.getCurrent(0).toString();
    }

    public void attack() {
        for (Creature e : game.getEnemies()) {
            if (e.getSkeletonBounds().aabbIntersectsSkeleton(getSkeletonBounds())) {
//                AddBloobParticlesForRender(e.getBloodParticle(), e.getX(), e.getY());
                e.setDamage(AP_MAX);
                System.out.println("atccking enemy " + e.getHP());
            }
        }
        sound_sword.play();
        //actHP -= 50;
    }

    public void run(UpdatePlayerEvent e) {
        setPosition(e.getX(), e.getY());
        setState(e.getAnimationState());
        setFlip(e.isFlip());
    }

    private void AddBloobParticlesForRender(ParticleEffect particle, float x, float y) {
//        ParticleEffect particle = new ParticleEffect();
//        particle.load(Gdx.files.internal("particles/blueblood.p"), Gdx.files.internal("particles"));
//        particle.setPosition(x, y);
        particle.start();
    }

    public void remoteUpdate(float delta) {
        super.update(delta);
        setCurrentState();

        state.update(delta);
        state.apply(skeleton);
    }

    public void update(float deltaTime) {
        super.update(deltaTime);
        shadowHeight = getSkeletonBounds().getHeight();
        shadowWidth = getSkeletonBounds().getWidth();

        if(isDead()){
            input.setState(States.Animation.DEAD);
            return;
        }

        if (alive) {
            // set v in x and y direction
            velocityX = input.get_stickX() * deltaTime * velocityX_MAX;
            velocityY = input.get_stickY() * deltaTime * velocityY_MAX;
            if (velocityX != 0 && velocityY != 0 && input.getState() == States.Animation.IDLE) {
                input.setState(States.Animation.RUN);
            }

            if (alive) {

//            if (!(input instanceof RemoteInput)) {
                // set v in x and y direction
                velocityX = input.get_stickX() * deltaTime * velocityX_MAX;
                velocityY = input.get_stickY() * deltaTime * velocityY_MAX;
                if (velocityX != 0 && velocityY != 0 && input.getState() == States.Animation.IDLE) {
                    input.setState(States.Animation.RUN);
                }

                if (velocityX == 0 && velocityY == 0 && input.getState() == States.Animation.RUN) {
                    input.setState(States.Animation.IDLE);
                }
            }


            if (velocityX == 0) {
                skeleton.setFlipX(flipped);
            }
            else {
                skeleton.setFlipX(velocityX < 0);
            }

            runningParticles.setPosition(x-10, y);

            // dash movement
            if(state.getCurrent(0).getAnimation().getName().equals("dash")){
                if(flipped) velocityX = -450  * deltaTime;
                else  velocityX = 450  * deltaTime;
            }

            x += velocityX;
            y += velocityY;
            if (runningParticles.isComplete() && input.getState() == States.Animation.RUN) {
                runningParticles.start();
            }

            runningParticles.update(deltaTime);

            setCurrentState();

            checkForNextToItem();

            flipped = skeleton.getFlipX();

            state.update(deltaTime);

            state.apply(skeleton);



            updateEvent.set(id, x, y, input.getState(), skeleton.getFlipX());
        }


    }


    public float getHeight() {
        return shadowHeight;
    }

    public float getWidth() {
        return shadowWidth;
    }

    private void checkForNextToItem() {
        int tolerance = 30;
        float playerPositionX = x + this.getSkeletonBounds().getWidth() / 2;
        float playerPositionY = y + this.getSkeletonBounds().getHeight() / 2;
        for (Item item : game.getItems()) {
            float itemPositionX = item.getX() + item.region.getTexture().getWidth() / 2;
            float itemPositionY = item.getY() + item.region.getTexture().getHeight() / 2;
            if (itemPositionX > playerPositionX - tolerance && itemPositionX < playerPositionX + tolerance) {
                if (itemPositionY > playerPositionY - tolerance && itemPositionY < playerPositionY + tolerance) {
                    collectItem(item);
                }
            }
        }
    }

    private void collectItem(Item item) {
        if (item instanceof HealthPotion)
            hp += 8;
        game.getItems().removeValue(item, true);
    }


    private void setCurrentState() {

//        System.out.println("set state: " + input.getState());

//        if (input != null) {

        String current = state.getCurrent(0).toString();

        if (current.equals("move") || current.equals("idle")) {
            if (input.getState() == States.Animation.JUMP && !current.equals("jump")) {
                double i =  Math.random();

                state.setAnimation(0, "jump", false);
                state.addAnimation(0, "idle", true, 0);
                sound_walk.stop();

                if(i <= 0.5)
                    jump.play();
                else
                   jump2.play();
                //            state.addAnimation(1, "move", true, jumpAnimation.getDuration() - 30);
                //            state.addAnimation(1, "move", false, 0);
            }
            if (input.getState() == States.Animation.ATTACK && !current.startsWith("attack")) {

                System.out.println("ATTACK!");
                attack();
                String attack = attackAnimations.get((int) (Math.random() * attackAnimations.size));
                state.setAnimation(0, attack, false);
                state.addAnimation(0, "idle", true, 0);
                sound_walk.stop();

            }
            if ((input.getState() == States.Animation.DASH_RIGHT || input.getState() == States.Animation.DASH_LEFT) && !current.equals("dash")) {
                if (input.getState() == States.Animation.DASH_RIGHT)
                    dashRight = true;
                else
                    dashRight = false;
                state.setAnimation(0, "dash", false);
                state.addAnimation(0, "idle", true, 0);
                sound_dash.play();
                sound_walk.stop();

            }
            if ((input.getState() == States.Animation.DEAD) && !current.equals("die")) {
                state.setAnimation(0, "die", false);
                sound_walk.stop();

            }
        }
        if (input.getState() == States.Animation.IDLE && !current.equals("idle")) {
            if (current.equals("move"))
                state.setAnimation(0, "idle", false);
            state.addAnimation(0, "idle", true, 0);
            sound_walk.stop();
        }
        if (input.getState() == States.Animation.RUN && current.equals("idle")) {
            state.setAnimation(0, "move", false);
            state.addAnimation(0, "move", true, 0);
            sound_walk.loop();
        }
        if (input.getState() != States.Animation.DEAD && input.getState() != States.Animation.RUN)
            input.setState(States.Animation.IDLE);
    }


    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public IGameInputController getInput() {
        return input;
    }

    public States.Animation getState() {
        return input.getState();
    }

    public void setState(States.Animation state) {
        input.setState(state);
    }

    public void setFlip(boolean flip) {
        skeleton.setFlipX(flip);
    }

    public int getID() {
        return id;
    }

    public void setID(final int id) {
        this.id = id;
    }

    public UpdatePlayerEvent getUpdateEvent() {
        return updateEvent;
    }
}

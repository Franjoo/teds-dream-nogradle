package com.angrynerds.tedsdream.gameobjects.skeletals;

import com.angrynerds.tedsdream.Assets;
import com.angrynerds.tedsdream.map.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;

/**
 * Author: Franz Benthin
 */
public class Creature extends Skeletal {

    // Movement Constants
    public static enum Move {
        WALK, RUN, HIDE
    }

    // creature stats
    protected float ap, hp;
    protected float apMax, hpMax;

    // movement
    protected Vector2 direction = new Vector2();

    // animation
    private Texture tex_shadow = Assets.instance().get("misc/shadow.png");
    private ParticleEffect bloodParticles = new ParticleEffect();
    protected AnimationStateData stateData;
    protected AnimationState state;

    //shadow
    boolean shadowSizes = false;
    private int shadowWidth;
    private int shadowHeight;

    public Creature(TextureAtlas atlas, String path, float scale, String skin, float ap, float hp, float x, float y) {
        super(atlas, path, scale, skin, x, y);

        this.ap = ap;
        this.hp = hp;

        // set to init values
        apMax = this.ap;
        hpMax = this.hp;

        // animation relevant
        stateData = new AnimationStateData(skeletonData);
        state = new AnimationState(stateData);
        state.setAnimation(0, skeletonData.getAnimations().first().getName(), true);
        bloodParticles.load(Gdx.files.internal("particles/blood.p"), Gdx.files.internal("particles"));
    }

    public Creature(TextureAtlas atlas, String path, float scale, String skin, float ap, float hp) {
        this(atlas, path, scale, skin, ap, hp, 0, 0);
    }

    public Creature(TextureAtlas atlas, String path, float scale, float ap, float hp) {
        this(atlas, path, scale, null, ap, hp);
    }

    public Creature(TextureAtlas atlas, String path, float ap, float hp) {
        this(atlas, path, 1, null, ap, hp);
    }

    public void setAnimation(String name, boolean loop) {
        state.setAnimation(0, name, loop);
    }

    public String getCurrentAnimationName() {
        return state.getCurrent(0).getAnimation().getName();
    }

    public void updateAnimations(float delta) {
        super.update(delta);
        if(!shadowSizes){
            shadowWidth = (int) skeletonBounds.getWidth() +150;
            shadowHeight = (int) skeletonBounds.getHeight()/3;
            shadowSizes = true;
        }


        // set flip
        skeleton.setFlipX(direction.x > 0);

        // update animation
        state.apply(skeleton);
        state.update(delta);
    }

    public void updatePosition(float delta) {
        x += direction.x * delta;
        y += direction.y * delta;

        // bounds
        if(y > Map.getProperties().boundsY_max) y = Map.getProperties().boundsY_max;
        else if(y < Map.getProperties().boundsY_min) y = Map.getProperties().boundsY_min;
    }

    @Override
    public void update(float delta) {
        updateAnimations(delta);
        updatePosition(delta);
        bloodParticles.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        // batch.draw(tex_shadow, skeletonBounds.getMinX(), y - tex_shadow.getHeight()/2, skeletonBounds.getWidth(), skeletonBounds.getHeight()/2);
        batch.draw(tex_shadow,x - shadowWidth/2, y - shadowHeight/2 - 10,shadowWidth,shadowHeight);
        // skeletal
        super.draw(batch);
        bloodParticles.draw(batch);
    }

    public void moveInDirection(float dx, float dy, float speed) {
        direction.set(dx, dy).scl(speed);
    }

    public void moveInDirection(Vector2 direction, float speed) {
        this.moveInDirection(direction.x, direction.y, speed);
    }

    public void setDamage(float dmg) {
        hp -= dmg;
        bloodParticles.setPosition(x, y);
        bloodParticles.start();
        if (hp <= 0)
            die();
    }

    public void playSound(String name){

    }

    public float getHP() {
        return hp;
    }

    public float getAP() {
        return ap;
    }

    public float getApMax() {
        return apMax;
    }

    public float getHpMax() {
        return hpMax;
    }

    private void die() {
        setAnimation("die", false);
    }

    public float getSpeed(Move speed) {
        switch (speed) {
            case WALK:
                return 100;
            case RUN:
                return 160;
            case HIDE:
                return 140;
        }

        return 0;
    }

    public float getSize() {
        return scale;
    }

}

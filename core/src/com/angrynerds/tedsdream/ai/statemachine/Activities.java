package com.angrynerds.tedsdream.ai.statemachine;

import com.angrynerds.tedsdream.gameobjects.GameObject;
import com.angrynerds.tedsdream.gameobjects.skeletals.Creature;
import com.angrynerds.tedsdream.gameobjects.skeletals.Player;
import com.angrynerds.tedsdream.screens.GameController;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;

/**
 * Author: Franz Benthin
 */
public class Activities {

    private static boolean DEBUG = true;
    private static ShapeRenderer DebugRenderer = new ShapeRenderer();

    private static GameController game;
    private static OrthographicCamera camera;
    private static Vector2 vec2; // helper vector

    public static void create(GameController game, OrthographicCamera camera) {
        Activities.game = game;
        Activities.camera = camera;

    }

    private Activities() {
    }

    // static helper methods
    private static float Distance(GameObject s1, GameObject s2) {
        return Vector2.len(s2.getX() - s1.getX(), s2.getY() - s1.getY());
    }

    private static Vector2 Direction(GameObject from, GameObject to) {
        return vec2.set(to.getX() - from.getX(), to.getY() - from.getY()).nor();
    }

    private static Player NearestPlayer(GameObject from) {
        float minDist = Distance(game.getPlayers().first(), from);
        Creature nearest = game.getPlayers().first();
        for (int i = 1; i < game.getPlayers().size; i++) {
            float currentDistance = Distance(game.getPlayers().get(i), from);
            if (currentDistance < minDist) {
                minDist = currentDistance;
                nearest = game.getPlayers().get(i);
            }
        }
        return (Player) nearest;
    }

    private static void TriggerAnimation(Creature actor, String name) {
        if (!(actor.getCurrentAnimationName().equals(name))) {
            actor.setAnimation(name, true);
        }
    }

    public static class CoverCreature extends Activity<Creature>{

        public CoverCreature(Creature actor, Creature target, GameController game) {
            super(actor, game);


        }

        @Override
        public boolean update(float delta) {
            return false;
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    // static activity classes
    public static class RunToObject extends Activity<Creature> {

        GameObject target;

        public RunToObject(Creature actor, GameObject target) {
            super(actor, Activities.game);

            this.target = target;
        }

        @Override
        public boolean update(float delta) {
            actor.moveInDirection(Direction(actor, target), actor.getSpeed(Creature.Move.RUN));
            return (actor.getX() == target.getX() && actor.getY() == target.getY());
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    public static class RunToPlayer extends Activity<Creature>{

        // path attributes
        Bezier<Vector2> path = new Bezier<>();
        Vector2 p1 = new Vector2();
        Vector2 p2 = new Vector2();

        public RunToPlayer(Creature actor) {
            super(actor, Activities.game);
            vec2 = new Vector2(actor.getX(), actor.getY());

        }

        @Override
        public boolean update(float delta) {
            Creature creature = actor;

            TriggerAnimation(creature, "move");

            if (game.getPlayers().size > 0) {
                Creature player = NearestPlayer(actor);

                final float dx = player.getX() - creature.getX();
                final float dy = player.getY() - creature.getY();
                vec2.set(dx, dy);

                float distance = vec2.len();
                if (distance > 30) {
                    vec2.nor();
                    creature.moveInDirection(vec2, creature.getSpeed(Creature.Move.WALK));

                    if (DEBUG) {
                        DebugRenderer.setProjectionMatrix(camera.combined);
                        DebugRenderer.begin(ShapeRenderer.ShapeType.Line);
                        DebugRenderer.setColor(0, 0, 0, 1);
                        DebugRenderer.line(actor.getX(), actor.getY(), player.getX(), player.getY());
                        DebugRenderer.end();
                    }

                    return false;
                }

            }

            creature.moveInDirection(0, 0, 0);
            return true;
        }

        @Override
        public float getScore() {
            return 10;
        }
    }

    public static class HideFromPlayer extends Activity<Creature> {

        private float hpHideValue;
        private float playerLastX, playerLastY;
        private float directionX, directionY;
        private float sameRouteTimeMax = 4f;
        private float time;

        public HideFromPlayer(Creature actor, float hpHideValue) {
            super(actor, Activities.game);

            this.hpHideValue = hpHideValue;

            vec2.set(20, 5).nor();
            directionX = vec2.x;
            directionY = vec2.y;
        }

        @Override
        public boolean update(float delta) {
            time += delta;

            Creature player = game.getPlayers().first();
            if (Distance(actor, player) <= 400) {

                TriggerAnimation(actor, "move");

                if (time > sameRouteTimeMax) {
                    time = 0;
                    Vector2 direction = getHideDirection();
                    directionX = direction.x;
                    directionY = direction.y;
                }

                actor.moveInDirection(directionX, directionY, actor.getSpeed(Creature.Move.HIDE));

                playerLastX = player.getX();
                playerLastY = player.getY();

                return false;
            }

            return false;
        }

        @Override
        public float getScore() {
            if (actor.getHP() <= hpHideValue) return 100;
            return 0;
        }


        private Vector2 getHideDirection() {
            Creature player = game.getPlayers().first();

            float playerCurrentX = player.getX();
            float playerCurrentY = player.getY();
            int dirX = ((int) (actor.getHP()) % 2 == 1) ? -1 : 1;

            vec2.set(playerCurrentX - playerLastX, playerCurrentY - playerLastY);
            vec2.x *= dirX;
            vec2.y *= -1;
            vec2.nor();

            if (vec2.len() < 1) vec2.set(directionX, directionY); // old direction


            return vec2;


        }
    }

    public static class WaitForPlayer extends Activity<Creature> {
        public WaitForPlayer(Creature actor) {
            super(actor, Activities.game);
        }

        @Override
        public boolean update(float delta) {
            return Distance(NearestPlayer(actor), actor) <= 400;
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    public static class AttackPlayer extends Activity<Creature> {
        public AttackPlayer(Creature actor) {
            super(actor, Activities.game);
        }

        @Override
        public boolean update(float delta) {
            Creature player = NearestPlayer(actor);
            if (player.getHP() > 0 && Distance(actor, player) < 30) {
                TriggerAnimation(actor, "attack");
                return false;
            }

            return true;
        }

        @Override
        public float getScore() {
            return 0;
        }
    }

    public static class FearfulAttackPlayer extends Activity<Creature> {

        private float hpFearValue;

        public FearfulAttackPlayer(Creature actor, float hpFearValue) {
            super(actor, Activities.game);
            this.hpFearValue = hpFearValue;
        }

        @Override
        public boolean update(float delta) {
            Creature player = NearestPlayer(actor);
            if (actor.getHP() >= hpFearValue && player.getHP() > 0 && Distance(actor, player) < 30) {
                TriggerAnimation(actor, "attack");
                return false;
            }

            return true;
        }

        @Override
        public float getScore() {
            if (actor.getHP() > hpFearValue) return 80;
            return 0;
        }
    }

}

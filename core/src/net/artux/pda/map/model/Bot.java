package net.artux.pda.map.model;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pdalib.profile.items.Weapon;

import java.util.Timer;
import java.util.TimerTask;

public class Bot extends Entity {

    private Vector2 target;
    private final Spawn spawn;
    float timer;

    public Bot(int id, final Vector2 position, final Spawn spawn, AssetManager skin, Mob mob, Player player) {
        super();

        setStartPosition(position);
        setPosition(position.x, position.y);

        this.id = id;
        MOVEMENT = 20f;
        velocity = new Vector2(0,0);
        if (player.member != null) {
            if (mob.group < 0 || player.member.relations.get(mob.group) < -2)
                sprite = new Sprite(skin.get("red.png", Texture.class));
            else if (player.member.relations.get(mob.group) > 2)
                sprite = new Sprite(skin.get("green.png", Texture.class));
            else
                sprite = new Sprite(skin.get("yellow.png", Texture.class));
        } else sprite = new Sprite(skin.get("yellow.png", Texture.class));
        setSize(8, 8);
        sprite.setOriginCenter();
        Weapon w = new Weapon();
        w.speed=5;
        w.damage=2;
        w.precision=30;

        setWeapon(w, 0);
        this.spawn = spawn;
    }

    @Override
    public void act(float delta) {

    if (target != null) {
        velocity.x = (target.x - getX()) / Math.abs(target.x - getX());
        velocity.y = (target.y - getY()) / Math.abs(target.y - getY());
    }

        if (getEnemy() != null) {
            double distance = getPosition().dst(getEnemy().getPosition());
            if (distance >= 300) {
                setEnemy(null);
                setDestination(getTarget());
                timerStarted = false;
                waiting = false;
            } else {
                setDestination(getEnemy().getPosition());
                waiting = false;
                if (distance < getHitDistance()) {
                    hit(delta);
                }
            }
        }
        if (!waiting && target != null) {
            super.act(delta);
            double distance = getPosition().dst(target);
            if (distance < 5) waiting = true;
        } else {
            if (!timerStarted) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setDestination(getTarget());
                        waiting = false;
                        timerStarted = false;
                    }
                }, 1000 * (Math.abs(random.nextLong() % 30)));
            }
            timerStarted = true;
        }

    }

    @Override
    public Vector2 getTarget() {
        return spawn.getRandomPoint(random);
    }

    public void setDestination(Vector2 point) {
        target = point;
    }

    public void hit(float dt) {
        if (timer>0.1f) {
            //registerHit(new Hit(Bot.this, getWeapon(), target));
            timer = 0;
        }else {
            timer += dt;
        }
    }

}

package net.artux.pda.map.model;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.BotStates;
import net.artux.pdalib.profile.items.Weapon;

import java.util.Timer;
import java.util.TimerTask;

public class Bot extends Entity<BotStates> {

    private Spawn spawn;
    public Vector2 movementTarget;

    public Bot(int id, final Vector2 position, Spawn spawn, AssetManager skin, Mob mob, Player player) {
        super(BotStates.FIND_TARGET);

        setStartPosition(position);
        movementTarget = position;
        setPosition(position.x, position.y);

        this.id = id;
        MOVEMENT = 20f;
        velocity = new Vector2(0,0);

        Weapon w = new Weapon();
        w.speed=5;
        w.damage=2;
        w.precision=30;
        setWeapon(w, 0);

        if (player.member != null) {
            if (mob.group < 0 || player.member.relations.get(mob.group) < -2)
                sprite = new Sprite(skin.get("red.png", Texture.class));
            else if (player.member.relations.get(mob.group) > 2)
                sprite = new Sprite(skin.get("green.png", Texture.class));
            else
                sprite = new Sprite(skin.get("yellow.png", Texture.class));
        } else sprite = new Sprite(skin.get("yellow.png", Texture.class));
        this.spawn = spawn;
        setSize(8, 8);
        sprite.setOriginCenter();
    }

    public void setMovementTarget(Vector2 movementTarget) {
        this.movementTarget = movementTarget;
    }

    public Vector2 getNewTarget() {
        double r = (double) spawn.getR()/2 + random.nextInt(spawn.getR());

        double angle = random.nextInt(360);

        Vector2 basePosition = spawn.getPosition();
        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x+x,basePosition.y+y);
    }


    public void hit(float dt) {
        /*if (timer>0.1f) {
            //registerHit(new Hit(Bot.this, getWeapon(), target));
            timer = 0;
        }else {
            timer += dt;
        }*/
    }

    public void stand(){
        velocity.x = 0;
        velocity.y = 0;
    }

    public void moveToTarget() {
        if (movementTarget != null) {

            Vector2 unit = new Vector2(movementTarget.x - getX(), movementTarget.y - getY());

            unit.scl(1/unit.len());
            velocity = unit;
            //velocity.x = (movementTarget.x - getX()) / Math.abs(movementTarget.x - getX());
            //velocity.y = (movementTarget.y - getY()) / Math.abs(movementTarget.y - getY());
        }
    }
}

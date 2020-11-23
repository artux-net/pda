package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Weapon;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.states.PlayState.distance;
import static net.artux.pda.map.states.PlayState.registerHit;

public abstract class Entity extends Actor {

    static float MOVEMENT = 0.4f;
    public boolean run = false;

    public int id;
    Vector2 position;
    Vector2 velocity;
    Sprite sprite;

    public double health = 100;

    private Vector2 target;
    boolean waiting;
    boolean timerStarted;
    Vector2 startPosition;

    private Entity enemy;

    private Armor armor;
    private Weapon weapon1;
    private Weapon weapon2;
    int weapon = 0;

    Entity(Vector2 position) {
        startPosition = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        boolean shot = false;
        return Objects.hash(run, id, position, velocity, sprite, health, target, waiting, timerStarted, startPosition, enemy, armor, weapon1, weapon2, weapon, shot);
    }

    @Override
    public void act(float delta) {
        update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        sprite.draw(batch);

    }


    public Vector2 getPosition() {
        return new Vector2(position.x - 16, position.y - 16);
    }

    public void setEnemy(Entity enemy) {
            this.enemy = enemy;
    }

    public Entity getEnemy() {
        return enemy;
    }

    public void setDestination(Vector2 point) {
        target = point;
    }

    float timer;

    public void hit(float dt) {
        if (timer>0.1f) {
            registerHit(new Hit(Entity.this,getWeapon(), target));
            timer = 0;
        }else {
            timer += dt;
        }
    }

    double getHitDistance() {
        if (getWeapon() == null)
            return 0;
        else {
            return getWeapon().precision * 5;
        }
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    void move() {
        velocity.x = (target.x - position.x) / Math.abs(target.x - position.x);
        velocity.y = (target.y - position.y) / Math.abs(target.y - position.y);

        position.add(velocity.x * MOVEMENT, velocity.y * MOVEMENT);
    }

    public void update(float dt) {

        if (getEnemy() != null) {
            double distance = distance(position, getEnemy().getPosition());
            if (distance >= 300) {
                setEnemy(null);
                setDestination(getTarget());
                timerStarted = false;
                waiting = false;
            } else {
                setDestination(getEnemy().position);
                waiting = false;
                if (distance < getHitDistance()) {
                    hit(dt);
                }
            }
        }
        if (!waiting && target != null) {
            move();
            double distance = distance(position, target);
            if (distance < 5) waiting = true;
            sprite.setPosition(position.x, position.y);
        }else {
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

    public Vector2 getTarget(){
        if (this instanceof Bot){
         return ((Bot) this).getTarget();
        }else return startPosition;
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public Weapon getWeapon() {
        if (weapon==0)
            return weapon1;
        else return weapon2;
    }

    public void setWeapon(Weapon weapon, int id) {
        if (id==0)
            weapon1 = weapon;
        else weapon2 = weapon;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void damage(double damage){
        health-=damage;
    }
}

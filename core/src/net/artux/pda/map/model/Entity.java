package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pdalib.arena.ServerEntity;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Weapon;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.states.PlayState.registerHit;

public abstract class Entity extends Actor {

    protected float MOVEMENT = 0.4f;
    public boolean run = false;

    public int id;
    Vector2 velocity = new Vector2();
    public Sprite sprite;

    public double health = 100;


    boolean waiting;
    boolean timerStarted;
    Vector2 startPosition;

    private Entity enemy;

    private Armor armor;
    private Weapon weapon1;
    private Weapon weapon2;
    int weapon = 0;

    public Entity(Vector2 position) {
        startPosition = position;
        setX(position.x);
        setY(position.y);
        setSize(32,32);
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
        return Objects.hash(run, id, velocity, sprite, health, waiting, timerStarted, startPosition, enemy, armor, weapon1, weapon2, weapon, shot);
    }

    @Override
    public void act(float delta) {
        System.out.println("moved by " + velocity.x * MOVEMENT +" : "+  velocity.y * MOVEMENT);
        System.out.println("moved by 60 times " + 60 * velocity.x * MOVEMENT +" : "+  60 * velocity.y * MOVEMENT);
        System.out.println("moved by " + 1/delta + " times " + 1/delta * velocity.x * MOVEMENT +" : "+ 1/delta * velocity.y * MOVEMENT);
        moveBy(velocity.x * MOVEMENT, velocity.y * MOVEMENT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(sprite, getX(), getY(), sprite.getOriginX(), sprite.getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }


    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public void setEnemy(Entity enemy) {
            this.enemy = enemy;
    }

    public Entity getEnemy() {
        return enemy;
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



    public Vector2 getTarget(){
        if (this instanceof Bot){
         return this.getTarget();
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

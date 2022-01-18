package net.artux.pda.map.model;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pda.map.BotStates;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Weapon;

import java.util.Objects;

public abstract class Entity<T extends State<? extends Entity<T>>> extends Actor {

    public int id;

    Vector2 velocity = new Vector2();
    protected float MOVEMENT = 20f;
    public boolean run = false;


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
    private DefaultStateMachine stateMachine;

    public Entity(T initialState) {
        setSize(32,32);
        stateMachine = new DefaultStateMachine(this, initialState);
        stateMachine.getCurrentState().enter(this);
    }

    public void setStartPosition(Vector2 startPosition) {
        this.startPosition = startPosition;
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
        return Objects.hash(run, id, velocity, sprite, health, waiting, timerStarted, startPosition, enemy, armor, weapon1, weapon2, weapon);
    }

    @Override
    public void act(float delta) {
        stateMachine.update();
        moveBy(delta*velocity.x * MOVEMENT, delta*velocity.y * MOVEMENT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(sprite, getX(), getY(), sprite.getOriginX(), sprite.getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public DefaultStateMachine getStateMachine() {
        return stateMachine;
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

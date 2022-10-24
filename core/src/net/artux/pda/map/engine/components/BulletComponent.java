package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class BulletComponent implements Component {

    private final Entity author;
    private Vector2 target;
    private float damage;
    private float lastDstToTarget;

    public BulletComponent(Entity author, Vector2 target, float damage) {
        this.author = author;
        this.target = target;
        this.damage = damage;
        lastDstToTarget = 1000;
    }

    public Entity getAuthor() {
        return author;
    }

    public Vector2 getTarget() {
        return target;
    }

    public float getDamage() {
        return damage;
    }

    public void setLastDstToTarget(float lastDstToTarget) {
        this.lastDstToTarget = lastDstToTarget;
    }

    public float getLastDstToTarget() {
        return lastDstToTarget;
    }
}
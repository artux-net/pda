package net.artux.pda.map.ecs.battle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class BulletComponent implements Component {

    private final Entity author;
    private final Entity target;
    private final Vector2 targetPosition;
    private final float damage;
    private float lastDstToTarget;

    public BulletComponent(Entity author, Entity target, Vector2 targetPosition, float damage) {
        this.author = author;
        this.target = target;
        this.targetPosition = targetPosition;
        this.damage = damage;
        lastDstToTarget = 1000;
    }

    public Entity getAuthor() {
        return author;
    }

    public Vector2 getTargetPosition() {
        return targetPosition;
    }

    public Entity getTarget() {
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
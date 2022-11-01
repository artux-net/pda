package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class VelocityComponent extends Vector2 implements Component {

    public boolean running;
    public float mass;
    private float damping = 0;

    public VelocityComponent() {
        super();
    }

    public VelocityComponent(float x, float y) {
        super(x, y);
    }

    public VelocityComponent linearDamping(float damp) {
        damping = damp;
        return this;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Vector2 getVelocity() {
        return this;
    }

    public void setVelocity(Vector2 velocity) {
        set(velocity);
    }

    public void update() {
        if (damping != 0) {
            if (x > 0)
                x -= damping;
            if (y > 0)
                y -= damping;

            if (x < 0)
                x = 0;
            if (y < 0)
                y = 0;
        }
    }

    public void applyForce(Vector2 force) {
        add(force);
    }

    public static void calculateImpulses(Vector2 position1, VelocityComponent velocity1, Vector2 position2, VelocityComponent velocity2) {
        // точка соприкосновения
        Vector2 temp = velocity1;
        velocity1.add(velocity2).scl(velocity1.mass / velocity2.mass);

    }
}
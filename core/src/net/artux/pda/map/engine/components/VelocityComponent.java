package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class VelocityComponent extends Vector2 implements Component {

    public boolean running;

    public VelocityComponent() {
        super();
    }

    public VelocityComponent(float x, float y) {
        super(x, y);
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
}
package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent extends Vector2 implements Component {

    boolean cameraVisible = false;

    public PositionComponent(Vector2 position) {
        super(position);
    }

    public PositionComponent(float x, float y) {
        super(x, y);
    }

    public Vector2 getPosition() {
        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isCameraVisible() {
        return cameraVisible;
    }

    public void setCameraVisible(boolean cameraVisible) {
        this.cameraVisible = cameraVisible;
    }

    public void setPosition(Vector2 position) {
        set(position);
    }
}
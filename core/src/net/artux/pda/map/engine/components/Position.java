package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Position extends Vector2 implements Component {

    public Position(Vector2 position) {
        super(position);
    }

    public Position(float x, float y) {
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

    public void setPosition(Vector2 position) {
        set(position);
    }
}
package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
    Vector2 position;

    public PositionComponent(Vector2 position) {
        this.position = new Vector2(position);
    }

    public PositionComponent(float x, float y) {
        position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getX(){
        return position.x;
    }

    public float getY(){
        return position.y;
    }
}
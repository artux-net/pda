package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {

    boolean cameraVisible = false;
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

    public boolean isCameraVisible() {
        return cameraVisible;
    }

    public void setCameraVisible(boolean cameraVisible) {
        this.cameraVisible = cameraVisible;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
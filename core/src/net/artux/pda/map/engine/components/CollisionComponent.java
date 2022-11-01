package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class CollisionComponent extends Circle implements Component {

    public CollisionComponent(float x, float y, float radius) {
        super(x, y, radius);
    }

    public CollisionComponent(Vector2 position, float radius) {
        super(position, radius);
    }
}
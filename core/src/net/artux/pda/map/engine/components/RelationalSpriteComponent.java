package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

public class RelationalSpriteComponent implements Component {

    private final float width;
    private final float height;
    private float alpha;

    public RelationalSpriteComponent(float width, float height) {
        this.width = width;
        this.height = height;
        alpha = 1;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return alpha;
    }
}
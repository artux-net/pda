package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteComponent implements Component {
    public Sprite sprite;

    public SpriteComponent(Texture texture) {
        sprite = new Sprite(texture);
    }


    public SpriteComponent(Texture texture, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
        sprite.setOriginCenter();
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
    }

    public void setAlpha(float a) {
        sprite.setAlpha(a);
    }

    public boolean isVisible() {
        return sprite.getColor().a > 0.01f;
    }

    public float getRotation() {
        return sprite.getRotation();
    }

    public void setRotation(float degrees) {
        sprite.setRotation(degrees + 90);
    }

}
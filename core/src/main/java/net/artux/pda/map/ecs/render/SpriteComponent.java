package net.artux.pda.map.ecs.render;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteComponent extends Sprite implements Component {

    public SpriteComponent(Texture texture, float width, float height) {
        super(texture);
        setSize(width, height);
        setOriginCenter();
    }

    public void setTexture(Texture texture) {
        super.setTexture(texture);
        setOriginCenter();
    }

}
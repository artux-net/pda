package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.model.components.player.UserVelocityInput;

public class SpriteComponent implements Component {
    public Sprite sprite;
    private UserVelocityInput userVelocityInput;

    public SpriteComponent(Texture texture) {
        sprite = new Sprite(texture);
    }


    public SpriteComponent(Texture texture, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
    }

    public SpriteComponent(UserVelocityInput userVelocityInput, Texture texture, float width, float height) {
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        this.userVelocityInput = userVelocityInput;
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
        sprite.setOriginCenter();
    }

    public void setSize(float width, float height){
        sprite.setSize(width, height);
    }

    public float getRotation(){
        if (userVelocityInput != null){
            Vector2 velocity = userVelocityInput.getVelocity();

            double degrees = Math.atan2(
                    -velocity.x,
                    velocity.y
            ) * 180.0d / Math.PI;
            if(velocity.x!=0 && velocity.y!=0)
                sprite.setRotation((float) degrees+90);
        }
        return sprite.getRotation();
    }

}
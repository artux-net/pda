package net.artux.pda.map.model.server;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pda.map.PlayerStates;
import net.artux.pda.map.model.Entity;
import net.artux.pdalib.arena.Position;

public class ServerEntity extends Entity {

    static float MOVEMENT = 5f;
    public boolean run = false;
    float time;
    public int id;
    Vector2 velocity = new Vector2();

    public ServerEntity(Vector2 position, AssetManager skin) {
        super(PlayerStates.STANDING);
        sprite = new Sprite(skin.get("yellow.png", Texture.class));
        sprite.setSize(12, 12);
    }

    public void setVelocity(Position velocity) {
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
    }

    public void act(float delta, float ping) {

        super.act(delta);


    }


    public void update(float dt) {
        if (velocity.x != 0 || velocity.y != 0){
            moveBy(velocity.x * MOVEMENT, velocity.y * MOVEMENT);
            System.out.println("ServerPlayer moveBy " + velocity.x * MOVEMENT + ":" + velocity.y * MOVEMENT);
        }
    }

    public Vector2 getPosition(){
        return new Vector2(getX(), getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (velocity !=null && velocity.dst(getPosition())>50) {
            double degrees = Math.atan2(
                    velocity.y - getY(),
                    velocity.x - getX()
            ) * 180.0d / Math.PI;
            sprite.setRotation((float) degrees - 90);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }
}

package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ServerPlayer extends Actor {

    static float MOVEMENT = 0.4f;
    public boolean run = false;

    public int id;
    Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();
    Sprite sprite;

    public double health = 100;


    public ServerPlayer(Vector2 position, AssetManager skin) {

        this.position = position;
        sprite = new Sprite(skin.get("yellow.png", Texture.class));
        sprite.setSize(12, 12);
        sprite.setPosition(0, 0);
        sprite.setOriginCenter();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }

    @Override
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
        sprite.setPosition(x,y);
    }
}

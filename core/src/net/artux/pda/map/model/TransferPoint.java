package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;

public class TransferPoint extends Actor {

    private final String title;
    private final Sprite sprite;
    private final HashMap<String, String> data = new HashMap<>();

    public TransferPoint(Transfer transfer, AssetManager manager){
        title = transfer.getMessage();
        setPosition(transfer.getPosition().x, transfer.getPosition().y);
        sprite = new Sprite(manager.get("transfer.png", Texture.class));
        sprite.setSize(32,32);
        data.put("map", String.valueOf(transfer.getTo()));
        data.put("pos", transfer.getToPosition());
    }

    public String getTitle() {
        return title;
    }

    public Vector2 getPosition(){
        return new Vector2(getX(), getY());
    }

    public HashMap<String, String> getData() {
        return data;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        sprite.draw(batch);
    }
}

package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

public class TransferPoint extends Actor {

    private String title;
    private Vector2 position;
    private Sprite sprite;
    private HashMap<String, String> data = new HashMap<>();

    public TransferPoint(Transfer transfer, Skin skin){
        title = transfer.getMessage();
        position = transfer.getPosition();
        sprite = new Sprite(skin.getSprite("transfer"));
        sprite.setSize(32,32);
        sprite.setPosition(position.x, position.y);
        data.put("map", String.valueOf(transfer.getTo()));
        data.put("pos", transfer.getToPosition());
    }

    public String getTitle() {
        return title;
    }

    public Vector2 getPosition() {
        return position;
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

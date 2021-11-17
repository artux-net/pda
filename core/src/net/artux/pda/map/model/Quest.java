package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;

public class Quest extends Actor {

    private int type;
    private String title;
    private Sprite sprite;
    private Vector2 position;
    private HashMap<String, String> data;

    public Quest(Point point, AssetManager skin){
        type = point.type;
        title = point.getTitle();
        position = point.getPosition();
        switch (type){
            case 0:
            case 1:
                sprite = new Sprite(skin.get("quest.png", Texture.class));
                sprite.setPosition(position.x, position.y);
                break;
            case 4:
                sprite = new Sprite(skin.get("seller.png", Texture.class));
                sprite.setPosition(position.x, position.y);
                break;
            case 5:
                sprite = new Sprite(skin.get("cache.png", Texture.class));
                sprite.setPosition(position.x, position.y);
                break;
            case 6:
                sprite = new Sprite(skin.get("quest1.png", Texture.class));
                sprite.setPosition(position.x, position.y);
                break;
        }
        setPosition(position.x, position.y);
        data = point.getData();
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (sprite!=null)
            sprite.draw(batch);
    }

    public Vector2 getPosition(){
        return position;
    }

    public HashMap<String, String> getData() {
        return data;
    }
}

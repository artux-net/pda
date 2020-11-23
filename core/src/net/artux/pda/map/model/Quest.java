package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

public class Quest extends Actor {

    public int type;
    public String title;
    Sprite sprite;
    public HashMap<String, String> data;
    Vector2 position;

    public Quest(Point point, Skin skin){
        type = point.type;
        title = point.getTitle();
        position = point.getPosition();
        if (type!=2 && type!=3) {
            sprite = new Sprite(skin.getSprite("quest"));
            sprite.setPosition(position.x, position.y);
        }
        data = point.getData();
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (sprite!=null) sprite.draw(batch);
    }

    public Vector2 getPosition(){
        return position;
    }

    public HashMap<String, String> getData() {
        return data;
    }
}

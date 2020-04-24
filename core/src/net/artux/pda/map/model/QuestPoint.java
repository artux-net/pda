package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class QuestPoint {

    private Texture texture;
    private Vector2 position;
    int chapter;
    int stage;

    public QuestPoint(Texture texture, float x, float y){
        this.texture = texture;
        position = new Vector2(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void draw(Batch batch){
        batch.draw(texture, position.x, position.y);
    }

}

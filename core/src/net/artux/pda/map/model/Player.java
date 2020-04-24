package net.artux.pda.map.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Player{

    private static float MOVEMENT = 0.4f;
    public boolean run = false;

    private Vector2 position;
    private Vector2 velocity;
    public Sprite sprite;


    public Player(float x, float y){
        position = new Vector2(x, y);
        velocity = new Vector2(0,0);
        sprite = new Sprite(new Texture("gg.png"), 0, 0, 2000, 2000);
        sprite.setSize(32, 32);
        sprite.setPosition(x,y);
        sprite.setOriginCenter();
    }

    public void draw(Batch batch){
        sprite.draw(batch);

    }

    public void setVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;

        double degrees = Math.atan2(
                -x,
                y
        ) * 180.0d / Math.PI;
        if(x!=0 && y!=0)
            sprite.setRotation((float) degrees);
    }


    Pixmap pixmap;
    public Texture bounds;

    public void setBoundsTexture(Texture texture){
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        pixmap = texture.getTextureData().consumePixmap();
        bounds = texture;

    }

    private boolean canMoveX(float x, float y){
        Color color = new Color(pixmap.getPixel(Math.round(x), bounds.getHeight() -Math.round(y)));
        /*System.out.println("X: "+color.toString());
        System.out.println(" R:" + color.r +  " G:" + color.g + " B:"+ color.b);*/
        return color.r != 1.0;
    }

    private boolean canMoveY(float x, float y){
        Color color = new Color(pixmap.getPixel(Math.round(x), bounds.getHeight() - Math.round(y)));
        /*System.out.println("Y: "+color.toString());
        System.out.println(" R:" + color.r +  " G:" + color.g + " B:"+ color.b);*/
        return color.r != 1.0;
    }

    public Vector2 getPosition(){
        return new Vector2(position.x -16, position.y-16);
    }

    public void update(float dt){
        if(run) MOVEMENT = 0.8f; else MOVEMENT = 0.4f;
        if (canMoveX(position.x+MOVEMENT*velocity.x, position.y)){
            position.add(MOVEMENT*velocity.x,0);
        }
        if (canMoveY(position.x, position.y + MOVEMENT*velocity.y)){
            position.add(0,MOVEMENT*velocity.y);
        }
        sprite.setPosition(position.x-16, position.y-16);
    }

}

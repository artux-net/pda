package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pdalib.Member;

import static net.artux.pda.map.states.PlayState.distance;

public class Player extends Entity{

    public Player(Vector2 playerPosition, Member member) {
        super(playerPosition);
        this.id = 1;
        MOVEMENT = 0.4f;
        position = playerPosition;
        velocity = new Vector2(0,0);
        sprite = new Sprite(new Texture("gg1.png"), 0, 0, 32, 32);
        sprite.setSize(32, 32);
        sprite.setPosition(playerPosition.x, playerPosition.y);
        sprite.setOriginCenter();
        if (member.getData()!=null) {
            setArmor(member.getData().getEquipment().getArmor());
            setWeapon(member.getData().getEquipment().getFirstWeapon(), 0);
            setWeapon(member.getData().getEquipment().getSecondWeapon(), 1);
        }
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

    public void update(float dt){
        if (getEnemy() != null) {
            double distance = distance(position, getEnemy().getPosition());
            if (distance >= 100) {
                setEnemy(null);
                setDestination(getTarget());
                timerStarted = false;
                waiting = false;
            } else {
                setDestination(getEnemy().position);
                waiting = false;
                if (distance < getHitDistance()) {
                    hit(dt);
                }
            }
        }

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

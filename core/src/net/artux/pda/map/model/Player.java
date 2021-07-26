package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.states.PlayState;
import net.artux.pda.map.states.State;
import net.artux.pdalib.Member;

public class Player extends Entity implements Disposable {

    Vector2 direction;
    Sprite directionSprite;
    OrthographicCamera camera;
    float zoom;
    Member member;
    boolean server = false;
    public Player(State state, Vector2 playerPosition, Member member, AssetManager skin) {
        super(playerPosition);
        camera = state.getCamera();
        zoom = camera.zoom;
        this.id = 1;
        MOVEMENT = 3f;
        velocity = new Vector2(0,0);
        sprite = new Sprite(skin.get("gg1.png", Texture.class));
        directionSprite = new Sprite(skin.get("direction.png", Texture.class));
        directionSprite.setOrigin(16, 0);
        sprite.setSize(32, 32);
        sprite.setOriginCenter();
        this.member = member;
        if (member!=null) {
            setArmor(member.getData().getEquipment().getArmor());
            setWeapon(member.getData().getEquipment().getFirstWeapon(), 0);
            setWeapon(member.getData().getEquipment().getSecondWeapon(), 1);
        }
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (direction !=null && direction.dst(getPosition())>50) {
            double degrees = Math.atan2(
                    direction.y - getY(),
                    direction.x - getX()
            ) * 180.0d / Math.PI;
            directionSprite.setRotation((float) degrees - 90);
            directionSprite.draw(batch, parentAlpha);
        }
    }

    public void setDirection(Vector2 direction){
        this.direction = direction;
    }

    @Override
    public void act(float delta) {

        if(run) {
            MOVEMENT = 0.8f;
            if (camera.zoom > 0.7*zoom) camera.zoom -= 0.007;
        } else {
            if (camera.zoom < zoom) camera.zoom += 0.005;
            MOVEMENT = 0.4f;
        }
        if (bounds!=null){
            if (canMoveX(getX()+MOVEMENT*velocity.x, getY())){
                System.out.println("act player");
                super.act(delta);
            }
            if (canMoveY(getX(), getY() + MOVEMENT*velocity.y)){
                System.out.println("act player");
                super.act(delta);
            }
        }else{
            System.out.println("act player");
            super.act(delta);
        }

        directionSprite.setPosition(getX(), getY()+16);
    }

    @Override
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;

        double degrees = Math.atan2(
                -x,
                y
        ) * 180.0d / Math.PI;
        if(x!=0 && y!=0)
            setRotation((float) degrees);
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

    public boolean canMoveX(float x, float y){
        Color color = new Color(pixmap.getPixel(Math.round(x), bounds.getHeight() -Math.round(y)));
        /*System.out.println("X: "+color.toString());
        System.out.println(" R:" + color.r +  " G:" + color.g + " B:"+ color.b);*/
        return color.r != 1.0;
    }

    public boolean canMoveY(float x, float y){

        Color color = new Color(pixmap.getPixel(Math.round(x), bounds.getHeight() - Math.round(y)));
        /*System.out.println("Y: "+color.toString());
        System.out.println(" R:" + color.r +  " G:" + color.g + " B:"+ color.b);*/
        return color.r != 1.0;
    }

    public void update(float dt){
       /* if (getEnemy() != null) {
            double distance = getPosition().dst(getEnemy().getPosition());
            if (distance >= 100) {
                setEnemy(null);
                setDestination(getTarget());
                timerStarted = false;
                waiting = false;
            } else {
                setDestination(getEnemy().getPosition());
                waiting = false;
                if (distance < getHitDistance()) {
                    hit(dt);
                }
            }
        }
*/

    }

    @Override
    public void dispose() {
        if (pixmap!=null)
            pixmap.dispose();
        if (bounds!=null)
            bounds.dispose();
    }
}

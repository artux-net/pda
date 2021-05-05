package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pdalib.profile.items.Weapon;

public class Hit extends Actor {

    static float MOVEMENT;

    Vector2 position;
    Vector2 velocity = new Vector2();
    Vector2 target;
    Sprite sprite;
    double damage;
    public Entity author;

    Hit(Entity entity, Weapon weapon, Vector2 target){
        MOVEMENT = weapon.speed;
        damage = weapon.damage;
        author= entity;
        this.target = target;
       // sprite = new Sprite(new Texture("quest.png"), 0, 0, 23, 23);
        sprite.setSize(16, 16);
        position = entity.getPosition();
        velocity.x = (target.x - position.x)/100;
        velocity.y = (target.y - position.y)/100;

        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update(delta);
    }

    void move(){

        position.add(velocity.x* MOVEMENT, velocity.y* MOVEMENT);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        sprite.draw(batch);
    }

    public void update(float ft){
        move();
        sprite.setPosition(position.x, position.y);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public double getDamage() {
        return damage;
    }
}

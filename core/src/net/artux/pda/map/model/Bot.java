package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pdalib.profile.items.Weapon;

import static com.badlogic.gdx.math.MathUtils.random;

public class Bot extends Entity {

    private Spawn spawn;
    public Bot(int id, Vector2 position, Spawn spawn) {
        super(position);
        this.id = id;
        MOVEMENT = 0.2f;
        this.position = position;
        velocity = new Vector2(0,0);
        if (id == 1)
            sprite = new Sprite(new Texture("red.png"), 0, 0, 128, 128);
        else if (id == 2)
            sprite = new Sprite(new Texture("green.png"), 0, 0, 128, 128);
        else if (id == 3)
            sprite = new Sprite(new Texture("yellow.png"), 0, 0, 128, 128);
        sprite.setSize(8, 8);
        sprite.setPosition(position.x, position.y);
        sprite.setOriginCenter();
        Weapon w = new Weapon();
        w.speed=5;
        w.damage=2;
        w.precision=30;

        setWeapon(w, 0);
        this.spawn = spawn;
    }

    @Override
    public Vector2 getTarget() {
        return spawn.getRandomPoint(random);
    }

}

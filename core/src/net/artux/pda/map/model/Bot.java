package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pdalib.profile.items.Weapon;

import static com.badlogic.gdx.math.MathUtils.random;

public class Bot extends Entity {

    private final Spawn spawn;
    public Bot(int id, final Vector2 position, final Spawn spawn, AssetManager skin, Mob mob, Player player) {
        super(position);
        this.id = id;
        MOVEMENT = 0.2f;
        velocity = new Vector2(0,0);

        if (mob.group < 0 || player.member.relations.get(mob.group)<-2 )
            sprite = new Sprite(skin.get("red.png", Texture.class));
        else if (player.member.relations.get(mob.group)>2)
            sprite = new Sprite(skin.get("green.png", Texture.class));
        else
            sprite = new Sprite(skin.get("yellow.png", Texture.class));
        setSize(8, 8);
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

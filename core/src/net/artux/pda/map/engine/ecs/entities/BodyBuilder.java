package net.artux.pda.map.engine.ecs.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface BodyBuilder {

    Body init(Vector2 position, World world);
}

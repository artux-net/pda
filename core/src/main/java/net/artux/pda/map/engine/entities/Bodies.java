package net.artux.pda.map.engine.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Bodies {

    private final static BodyBuilder stalker = (position, world) -> {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        Body body = world.createBody(bodyDef);
        body.setLinearDamping(10);
        CircleShape circle = new CircleShape();
        circle.setRadius(3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0;

        body.createFixture(fixtureDef);

        circle.dispose();
        return body;
    };

    public static Body stalker(Vector2 position, World world) {
        return stalker.init(position, world);
    }

    public static Body mutant(Vector2 target, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(target);

        Body body = world.createBody(bodyDef);
        body.setLinearDamping(2);
        body.getMassData().mass = 100f;
        CircleShape circle = new CircleShape();
        circle.setRadius(3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 40;
        fixtureDef.friction = 0;

        body.createFixture(fixtureDef);

        circle.dispose();
        return body;
    }
}

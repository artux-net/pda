package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.pda.map.engine.ecs.entities.BodyBuilder;

public class BodyComponent implements Component {

    public final Body body;

    public BodyComponent(BodyBuilder bodyBuilder, Vector2 position, World world) {
        this.body = bodyBuilder.init(position, world);
    }

    public BodyComponent(Body body) {
        this.body = body;
    }

    public BodyComponent(Vector2 vector2, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(vector2.x, vector2.y);
        body = world.createBody(bodyDef);
    }

    public BodyComponent(Vector2 vector2, BodyDef.BodyType type, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(vector2);
        bodyDef.type = type;
        bodyDef.bullet = true;
        body = world.createBody(bodyDef);
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getX() {
        return body.getPosition().x;
    }

    public float getY() {
        return body.getPosition().y;
    }

    public BodyComponent velocity(double vX, double vY) {
        body.setLinearDamping(0);
        body.setLinearVelocity((float) vX, (float) vY);
        //body.setTransform(body.getPosition().x, body.getPosition().y, 30f);
        return this;
    }

    public BodyComponent impulse(float x, float y) {
        body.applyForceToCenter(x, y, true);
        return this;
    }
}
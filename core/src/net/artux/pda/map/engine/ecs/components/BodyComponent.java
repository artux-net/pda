package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class BodyComponent implements Component {

    public final Body body;

    public BodyComponent(BodyBuilder bodyBuilder) {
        this.body = bodyBuilder.init();
    }

    public BodyComponent(Vector2 vector2, World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(vector2);
        bodyDef.active = false;
        body = world.createBody(bodyDef);
    }

    public BodyComponent(Vector2 vector2, BodyDef.BodyType type, World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(vector2);
        bodyDef.type = type;
        body = world.createBody(bodyDef);
    }


    public Body getBody() {
        return body;
    }

    public interface BodyBuilder {
        Body init();
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

}
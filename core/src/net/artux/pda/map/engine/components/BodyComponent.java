package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class BodyComponent implements Component {

    public final Body body;

    public BodyComponent(BodyBuilder bodyBuilder) {
        this.body = bodyBuilder.init();
    }

    public Body getBody() {
        return body;
    }

    public interface BodyBuilder {
        Body init();
    }

}
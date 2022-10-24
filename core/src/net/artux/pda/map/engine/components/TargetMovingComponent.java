package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TargetMovingComponent implements Component {
    public Targeting targeting;

    public TargetMovingComponent(Targeting targeting) {
        this.targeting = targeting;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public interface Targeting {

        Vector2 getTarget();

    }

}
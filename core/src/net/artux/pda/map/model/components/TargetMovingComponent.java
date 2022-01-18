package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class TargetMovingComponent implements Component {
    public Targeting targeting;
    public Vector2 movementTarget;

    public TargetMovingComponent(Targeting targeting) {
        this.targeting = targeting;
    }

    public void setNextTarget(){
        setMovementTarget(targeting.getTarget());
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setMovementTarget(Vector2 movementTarget) {
        this.movementTarget = movementTarget;
    }

    public interface Targeting{

        Vector2 getTarget();

    }

}
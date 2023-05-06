package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TargetMovingComponent implements Component {
    private Targeting targeting;
    private GroupComponent groupComponent;

    public TargetMovingComponent(GroupComponent groupComponent) {
        this.groupComponent = groupComponent;
    }

    public TargetMovingComponent(Targeting targeting) {
        this.targeting = targeting;
    }

    public Vector2 nextTarget() {
        if (groupComponent != null)
            if (targeting == null || targeting != groupComponent.getTargeting())
                targeting = groupComponent.getTargeting();
        return targeting.getTarget();
    }

    public interface Targeting {
        Vector2 getTarget();
    }

}
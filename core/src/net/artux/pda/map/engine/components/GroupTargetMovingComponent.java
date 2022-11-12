package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class GroupTargetMovingComponent implements Component {
    public GroupComponent groupComponent;

    public GroupTargetMovingComponent(GroupComponent groupComponent) {
        this.groupComponent = groupComponent;
    }

    public Vector2 nextTarget() {
        return groupComponent.getTargeting().getTarget();
    }

    public interface Targeting {

        Vector2 getTarget();

    }

}
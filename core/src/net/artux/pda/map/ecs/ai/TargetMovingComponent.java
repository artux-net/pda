package net.artux.pda.map.ecs.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TargetMovingComponent implements Component {
    private Targeting targeting;
    private StalkerGroup stalkerGroup;

    public TargetMovingComponent(StalkerGroup stalkerGroup) {
        this.stalkerGroup = stalkerGroup;
    }

    public TargetMovingComponent(Targeting targeting) {
        this.targeting = targeting;
    }

    public Vector2 nextTarget() {
        if (stalkerGroup != null)
            if (targeting == null || targeting != stalkerGroup.getTargeting())
                targeting = stalkerGroup.getTargeting();
        return targeting.getTarget();
    }

    public interface Targeting {
        Vector2 getTarget();
    }

}
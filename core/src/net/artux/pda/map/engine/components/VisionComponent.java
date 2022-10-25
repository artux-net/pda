package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.LinkedList;

public class VisionComponent implements Component {
    private final LinkedList<Entity> visibleEntities;

    public VisionComponent() {
        visibleEntities = new LinkedList<>();
    }

    public void addVisibleEntity(Entity entity) {
        visibleEntities.add(entity);
    }

    public LinkedList<Entity> getVisibleEntities() {
        return visibleEntities;
    }

    public boolean isSeeing(Entity entity) {
        return visibleEntities.contains(entity);
    }

    public void clear() {
        visibleEntities.clear();
    }
}
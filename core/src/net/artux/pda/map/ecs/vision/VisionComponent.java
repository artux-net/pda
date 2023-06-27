package net.artux.pda.map.ecs.vision;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.map.ecs.vision.VisionSystem;

import java.util.LinkedList;

/**
 * Компонент отражающий видимые сущностью другие сущности
 * работает через {@link VisionSystem}
 */
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
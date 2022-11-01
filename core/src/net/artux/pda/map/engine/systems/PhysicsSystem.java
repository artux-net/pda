package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Intersector;

import net.artux.pda.map.engine.components.CollisionComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;

public class PhysicsSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);
    private ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public PhysicsSystem() {
        super(Family.all(CollisionComponent.class, VisionComponent.class, PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pm.get(entity);
        CollisionComponent collisionComponent = cm.get(entity);
        VisionComponent visionComponent = vcm.get(entity);

        collisionComponent.setPosition(positionComponent);
        for (Entity visibleEntity : visionComponent.getVisibleEntities()) {
            if (cm.has(visibleEntity)) {
                CollisionComponent collisionOfVisible = cm.get(entity);
                if (Intersector.overlaps(collisionComponent, collisionOfVisible)) {
                    VelocityComponent velocityComponent = vm.get(entity);
                    VelocityComponent velocityComponent1 = vm.get(visibleEntity);
                    PositionComponent positionComponent1 = pm.get(visibleEntity);

                    VelocityComponent.calculateImpulses(positionComponent, velocityComponent, positionComponent1, velocityComponent1);
                }
            }
        }
    }
}

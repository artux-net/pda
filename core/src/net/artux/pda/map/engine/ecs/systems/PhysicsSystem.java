package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Intersector;

import net.artux.pda.map.engine.ecs.components.CollisionComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;

public class PhysicsSystem extends IteratingSystem {

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);
    private ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public PhysicsSystem() {
        super(Family.all(CollisionComponent.class, VisionComponent.class, BodyComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent bodyComponent = pm.get(entity);
        CollisionComponent collisionComponent = cm.get(entity);
        VisionComponent visionComponent = vcm.get(entity);

        collisionComponent.setPosition(bodyComponent.getPosition());
        for (Entity visibleEntity : visionComponent.getVisibleEntities()) {
            if (cm.has(visibleEntity)) {
                CollisionComponent collisionOfVisible = cm.get(entity);
                if (Intersector.overlaps(collisionComponent, collisionOfVisible)) {
                    VelocityComponent velocityComponent = vm.get(entity);
                    VelocityComponent velocityComponent1 = vm.get(visibleEntity);
                    BodyComponent bodyComponent1 = pm.get(visibleEntity);

                    //VelocityComponent.calculateImpulses(bodyComponent, velocityComponent, bodyComponent1, velocityComponent1);
                }
            }
        }
    }
}

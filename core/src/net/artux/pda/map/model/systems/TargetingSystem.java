package net.artux.pda.map.model.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.model.components.PositionComponent;
import net.artux.pda.map.model.components.SpriteComponent;
import net.artux.pda.map.model.components.TargetMovingComponent;
import net.artux.pda.map.model.components.VelocityComponent;

public class TargetingSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<TargetMovingComponent> tmm = ComponentMapper.getFor(TargetMovingComponent.class);


    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(VelocityComponent.class, PositionComponent.class, TargetMovingComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            PositionComponent positionComponent = pm.get(entity);
            VelocityComponent velocityComponent = vm.get(entity);
            TargetMovingComponent targetMovingComponent = tmm.get(entity);

            if (targetMovingComponent.movementTarget!=null){
                Vector2 unit = new Vector2(targetMovingComponent.movementTarget.x - positionComponent.getX(),
                        targetMovingComponent.movementTarget.y - positionComponent.getY());

                unit.scl(1/unit.len());
                velocityComponent.setVelocity(unit);
            }else
                velocityComponent.setVelocity(Vector2.Zero);

        }
    }
}

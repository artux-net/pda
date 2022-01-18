package net.artux.pda.map.model.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.model.components.PlayerComponent;
import net.artux.pda.map.model.components.PositionComponent;

public class CameraFollowingSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<PlayerComponent> cm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            PositionComponent positionComponent = pm.get(entity);
            PlayerComponent playerComponent = cm.get(entity);

            playerComponent.camera.position.set(positionComponent.getPosition(), 0);
        }
    }
}

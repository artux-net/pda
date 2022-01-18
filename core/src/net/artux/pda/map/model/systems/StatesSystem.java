package net.artux.pda.map.model.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.model.components.StatesComponent;

public class StatesSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(StatesComponent.class).get());
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            sm.get(entity).stateMachine.getCurrentState().enter(entity);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            sm.get(entity).stateMachine.update();
        }
    }
}

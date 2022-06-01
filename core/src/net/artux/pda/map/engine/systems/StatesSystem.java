package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.StatesComponent;

public class StatesSystem extends EntitySystem implements Disposable {

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

    @Override
    public void dispose() {
        entities = getEngine().getEntitiesFor(Family.all(StatesComponent.class).get());
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (sm.get(entity).stateMachine.getCurrentState() instanceof Disposable)
                ((Disposable)sm.get(entity).stateMachine.getCurrentState()).dispose();
        }
    }
}

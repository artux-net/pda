package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageManager;

import net.artux.pda.map.engine.components.StatesComponent;

public class StatesSystem extends IteratingSystem {

    private ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);

    public StatesSystem() {
        super(Family.all(StatesComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);
            sm.get(entity).stateMachine.getCurrentState().enter(entity);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        sm.get(entity).stateMachine.update();
        MessageManager.getInstance().update();
    }

}

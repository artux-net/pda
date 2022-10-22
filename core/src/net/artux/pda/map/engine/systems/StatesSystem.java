package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.artux.pda.map.engine.components.StateMachineComponent;

public class StatesSystem extends IteratingSystem {

    private ComponentMapper<StateMachineComponent> sm = ComponentMapper.getFor(StateMachineComponent.class);

    public StatesSystem(){
        super(Family.one(StateMachineComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        sm.get(entity).stateMachine.update();
    }
}

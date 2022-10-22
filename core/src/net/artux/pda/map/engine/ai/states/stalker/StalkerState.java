package net.artux.pda.map.engine.ai.states.stalker;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;

public abstract class StalkerState extends BotState {

    protected ComponentMapper<TargetMovingComponent> tmm = ComponentMapper.getFor(TargetMovingComponent.class);
    protected ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    protected ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    protected ComponentMapper<VelocityComponent> vcm = ComponentMapper.getFor(VelocityComponent.class);
    protected ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    protected ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);

    public StalkerState(StateMachine<Entity, BotState> stateMachine) {
        super(stateMachine);
    }
}

package net.artux.pda.map.engine.ai.states.stalker;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

import net.artux.pda.map.engine.components.MoodComponent;

public class GlobalGuardingState extends StalkerState {


    public GlobalGuardingState(StateMachine<Entity, BotState> stateMachine) {
        super(stateMachine);
    }

    @Override
    public void enter(Entity entity) {
        stateMachine.changeState(new StandingState(stateMachine));
    }

    @Override
    public void update(Entity entity) {
        MoodComponent moodComponent = mm.get(entity);
        if (moodComponent.hasEnemy()) {
            stateMachine.setGlobalState(new GlobalAttackingState(stateMachine));
        }
    }

    @Override
    public void exit(Entity entity) {

    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}

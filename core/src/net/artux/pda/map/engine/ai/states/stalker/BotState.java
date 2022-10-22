package net.artux.pda.map.engine.ai.states.stalker;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;

public abstract class BotState implements State<Entity> {

    protected final StateMachine<Entity, BotState> stateMachine;

    protected BotState(StateMachine<Entity, BotState> stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void changeState(BotState state) {
        stateMachine.changeState(state);
    }

}

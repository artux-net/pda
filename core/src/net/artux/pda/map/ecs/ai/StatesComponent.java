package net.artux.pda.map.ecs.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageDispatcher;

import net.artux.pda.map.ecs.ai.states.MutantState;
import net.artux.pda.map.ecs.ai.states.StalkerState;

public class StatesComponent extends DefaultStateMachine<Entity, State<Entity>> implements Component {

    private final MessageDispatcher dispatcher;

    public StatesComponent(Entity owner, MessageDispatcher dispatcher, State<Entity> initState, State<Entity> globalState) {
        super(owner, initState, globalState);
        this.dispatcher = dispatcher;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public String getStateTitle() {
        if (getCurrentState() instanceof StalkerState state) {
            StalkerState globalState = (StalkerState) getGlobalState();
            return state.name() + " (" + globalState.name()+ ")";
        }

        if (getCurrentState() instanceof MutantState state) {
            MutantState globalState = (MutantState) getGlobalState();
            return state.name() + " (" + globalState.name()+ ")";
        }
        return "";
    }

}

package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;

import net.artux.pda.map.engine.MessagingCodes;
import net.artux.pda.map.engine.components.states.MyStateMachine;
import net.artux.pda.map.engine.components.states.StalkerState;

public class StatesComponent extends MyStateMachine<Entity, StalkerState> implements Component {

    private final MessageDispatcher dispatcher;

    public StatesComponent(Entity owner, MessageDispatcher dispatcher) {
        super(owner, StalkerState.STANDING, StalkerState.GUARDING);
        this.dispatcher = dispatcher;
        dispatcher.addListener(this, MessagingCodes.ATTACKED);
    }

    public StatesComponent(Entity owner, MessageDispatcher dispatcher, StalkerState initState, StalkerState globalState) {
        super(owner, initState, globalState);
        this.dispatcher = dispatcher;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }
}

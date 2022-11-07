package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageDispatcher;

public class StatesComponent extends DefaultStateMachine<Entity, State<Entity>> implements Component {

    private final MessageDispatcher dispatcher;

    public StatesComponent(Entity owner, MessageDispatcher dispatcher, State<Entity> initState, State<Entity> globalState) {
        super(owner, initState, globalState);
        this.dispatcher = dispatcher;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }
}

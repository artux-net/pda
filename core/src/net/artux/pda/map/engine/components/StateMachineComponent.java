package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;

public class StateMachineComponent<E, T extends State<E>> implements Component {

    public DefaultStateMachine<E, T> stateMachine;

    public StateMachineComponent(E owner, T initState, T globalState) {
        stateMachine = new DefaultStateMachine<>(owner, initState, globalState);
    }
}

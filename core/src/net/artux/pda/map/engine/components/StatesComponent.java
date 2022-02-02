package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.State;

import net.artux.pda.map.engine.components.states.MyStateMachine;

public class StatesComponent<E, T extends State<E>> implements Component {

    public MyStateMachine<E, T> stateMachine;

    public StatesComponent(E owner, T initState, T globalState) {
        stateMachine = new MyStateMachine<>(owner, initState, globalState);
    }
}

package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;

import net.artux.pda.map.model.MyStateMachine;

public class StatesComponent<E, T extends State<E>> implements Component {

    public MyStateMachine<E, T> stateMachine;

    public StatesComponent(E owner, T initState, T globalState) {
        stateMachine = new MyStateMachine<>(owner, initState, globalState);
    }
}

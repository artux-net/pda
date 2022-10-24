package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.map.engine.components.states.BotStatesAshley;
import net.artux.pda.map.engine.components.states.MyStateMachine;

public class StatesComponent implements Component {

    public MyStateMachine<Entity, BotStatesAshley> stateMachine;

    public StatesComponent(Entity owner, BotStatesAshley initState, BotStatesAshley globalState) {
        stateMachine = new MyStateMachine<>(owner, initState, globalState);
    }
}

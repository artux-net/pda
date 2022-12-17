package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameStateController {

    private final Stack<net.artux.pda.map.states.State> states;
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    @Inject
    public GameStateController() {
        states = new Stack<>();
    }

    public void push(net.artux.pda.map.states.State state) {
        if (!states.empty())
            states.peek().stop();
        states.push(state);
        states.peek().handleInput();
    }

    public void resume() {
                states.peek().resume();
    }

    public State peek() {
        return states.peek();
    }

    public void set(State state) {
        states.pop().dispose();
        states.push(state);
        states.peek().handleInput();
        states.peek().resume();
    }

    public void dispose() {
        for (State state : states) {
            state.dispose();
        }
    }

    public void update(float dt) {
        if (states.size() > 0)
            states.peek().update(dt);
    }

    public void resize(int width, int height) {
        if (states.size() > 0)
            states.peek().resize(width, height);
    }

    public void render() {
        if (states.size() > 0)
            states.peek().render();
    }

    public void addInputProcessor(InputProcessor inputProcessor) {
        if (!multiplexer.getProcessors().contains(inputProcessor, false))
            multiplexer.addProcessor(inputProcessor);
        if (multiplexer.size() >= 1)
            Gdx.input.setInputProcessor(multiplexer);
    }

    public void removeInputProcessor(InputProcessor inputProcessor) {
        if (multiplexer.getProcessors().contains(inputProcessor, false))
            multiplexer.removeProcessor(inputProcessor);
    }
}

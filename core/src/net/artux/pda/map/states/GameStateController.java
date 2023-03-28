package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import net.artux.pda.map.utils.di.components.CoreComponent;

import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameStateController {

    private final Stack<net.artux.pda.map.states.State> states;
    private final InputMultiplexer multiplexer = new InputMultiplexer();
    private final CoreComponent coreComponent;

    @Inject
    public GameStateController(CoreComponent coreComponent) {
        states = new Stack<>();
        this.coreComponent = coreComponent;
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
        State current = states.pop();
        current.stop();
        current.dispose();
        clearInputProcessors();
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
        try {
            if (states.size() > 0)
                states.peek().update(dt);
        } catch (Exception e) {
            ErrorState errorState = coreComponent.getErrorState();
            errorState.setThrowable(e);
            set(errorState);
        }
    }

    public void resize(int width, int height) {
        if (states.size() > 0)
            states.peek().resize(width, height);
    }

    public void render() {
        try {
            if (states.size() > 0)
                states.peek().render();
        } catch (Exception e) {
            ErrorState errorState = coreComponent.getErrorState();
            errorState.setThrowable(e);
            set(errorState);
        }
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

    public void clearInputProcessors() {
        multiplexer.clear();
    }
}

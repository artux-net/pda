package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.models.UserGdx;
import net.artux.pda.map.platform.PlatformInterface;

import java.util.HashMap;
import java.util.Stack;

public class GameStateManager {

    private final PlatformInterface platformInterface;
    HashMap<String, Object> bundle = new HashMap<>();
    private final Stack<net.artux.pda.map.states.State> states;
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    public GameStateManager(PlatformInterface platformInterface){
        states = new Stack<>();
        this.platformInterface = platformInterface;
    }

    public void put(String key, Object o){
        bundle.put(key, o);
    }

    public Object get(String key){
        return bundle.get(key);
    }

    public PlatformInterface getPlatformInterface() {
        return platformInterface;
    }

    public void push(net.artux.pda.map.states.State state){
        if (!states.empty())
            states.peek().stop();
        states.push(state);
        states.peek().handleInput();
    }

    public State peek(){
        return states.peek();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
        states.peek().handleInput();
    }

    public void dispose(){
        for (State state: states){
            state.dispose();
        }
        for (Object obj : bundle.values()) {
            if (obj instanceof Disposable)
                ((Texture) obj).dispose();
        }
    }

    public void update(float dt){
        if (states.size()>0)
            states.peek().update(dt);
    }

    public void resize(int width, int height){
        if (states.size()>0)
            states.peek().resize(width, height);
    }

    public void render(){
        if (states.size()>0)
            states.peek().render();
    }

    public UserGdx getMember() {
        return (UserGdx) bundle.get("user");
    }

    public void addInputProcessor(InputProcessor inputProcessor){
        if (!multiplexer.getProcessors().contains(inputProcessor, false))
            multiplexer.addProcessor(inputProcessor);
        if (multiplexer.size()>=1)
            Gdx.input.setInputProcessor(multiplexer);
    }

    public void removeInputProcessor(InputProcessor inputProcessor){
        if (multiplexer.getProcessors().contains(inputProcessor, false))
            multiplexer.removeProcessor(inputProcessor);
    }
}

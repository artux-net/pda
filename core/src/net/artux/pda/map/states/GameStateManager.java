package net.artux.pda.map.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;

import java.util.Stack;

public class GameStateManager {

    private PlatformInterface platformInterface;
    private Map map;
    private Stack<net.artux.pda.map.states.State> states;

    public GameStateManager(PlatformInterface platformInterface, Map map){
        states = new Stack<net.artux.pda.map.states.State>();
        this.platformInterface = platformInterface;
        this.map = map;
    }

    public PlatformInterface getPlatformInterface() {
        return platformInterface;
    }

    public Map getMap() {
        return map;
    }

    public void push(net.artux.pda.map.states.State state){
        states.push(state);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void resize(int width, int height){
        states.peek().resize(width, height);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }
}

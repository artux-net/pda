package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pdalib.Member;

import java.util.Stack;

public class GameStateManager {

    private PlatformInterface platformInterface;
    private Map map;
    private Stack<net.artux.pda.map.states.State> states;
    private Member member;
    private InputMultiplexer multiplexer = new InputMultiplexer();

    public GameStateManager(PlatformInterface platformInterface, Map map, Member member){
        states = new Stack<net.artux.pda.map.states.State>();
        this.platformInterface = platformInterface;
        this.map = map;
        this.member = member;
    }

    public PlatformInterface getPlatformInterface() {
        return platformInterface;
    }

    public Map getMap() {
        return map;
    }

    public void push(net.artux.pda.map.states.State state){
        if (!states.empty())
            states.peek().stop();
        states.push(state);
        states.peek().handleInput();
    }

    public void pop(){
        states.peek().stop();
        states.pop().dispose();
        states.peek().handleInput();
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

    public Member getMember() {
        return member;
    }

    public void addInputProcessor(InputProcessor inputProcessor){
        if (!multiplexer.getProcessors().contains(inputProcessor, true))
            multiplexer.addProcessor(inputProcessor);
        if (multiplexer.size()>=1)
            Gdx.input.setInputProcessor(multiplexer);
    }

    public void removeInputProcessor(InputProcessor inputProcessor){
        if (multiplexer.getProcessors().contains(inputProcessor, true))
            multiplexer.removeProcessor(inputProcessor);
    }
}

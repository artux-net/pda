package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pdalib.Member;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class GameStateManager {

    private final PlatformInterface platformInterface;
    HashMap<String, Object> bundle = new HashMap<>();
    private final Stack<net.artux.pda.map.states.State> states;
    private Member member;
    private final InputMultiplexer multiplexer = new InputMultiplexer();

    BitmapFont russianFont;

    public void setRussianFont(BitmapFont russianFont) {
        this.russianFont = russianFont;
    }

    public BitmapFont getRussianFont() {
        return russianFont;
    }

    public GameStateManager(PlatformInterface platformInterface, Member member){
        states = new Stack<>();
        this.platformInterface = platformInterface;
        this.member = member;
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

    public void pop(){
        states.peek().stop();
        states.pop();
        states.peek().handleInput();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    public void dispose(){
        for (State state: states){
            state.dispose();
        }
        russianFont.dispose();
        member = null;
    }

    public void update(float dt){
        if (states.size()>0)
            states.peek().update(dt);
    }

    public void resize(int width, int height){
        if (states.size()>0)
            states.peek().resize(width, height);
    }

    public void render(SpriteBatch sb){
        if (states.size()>0)
            states.peek().render(sb);
    }

    public Member getMember() {
        return member;
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

package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import net.artux.pdalib.Member;

public abstract class State {

    protected OrthographicCamera camera;
    protected Vector3 mouse;
    protected GameStateManager gsm;

    protected float w = Gdx.graphics.getWidth();
    protected float h = Gdx.graphics.getHeight();

    public State(GameStateManager gsm){
        this.gsm = gsm;
        camera = new OrthographicCamera();
        mouse = new Vector3();
    }

    protected abstract void handleInput();
    protected abstract void stop();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void resize(int width, int height);
    public abstract void dispose();

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Member getMember(){
        return (Member) gsm.get("member");
    }
}
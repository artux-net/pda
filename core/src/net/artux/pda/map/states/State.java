package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;

import net.artux.pda.model.user.UserModel;


public abstract class State {

    public static GameStateManager gsm;

    protected float w = Gdx.graphics.getWidth();
    protected float h = Gdx.graphics.getHeight();

    public State(GameStateManager gsm) {
        this.gsm = gsm;
    }

    protected abstract void handleInput();

    protected abstract void stop();

    public abstract void update(float dt);

    public abstract void render();

    public abstract void resize(int width, int height);

    public abstract void dispose();

    public UserModel getMember() {
        return (UserModel) gsm.get("member");
    }
}
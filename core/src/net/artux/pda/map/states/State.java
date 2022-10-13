package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;

import net.artux.pda.map.DataRepository;
import net.artux.pda.model.user.UserModel;


public abstract class State {

    protected final GameStateManager gsm;
    protected final DataRepository dataRepository;

    protected float w = Gdx.graphics.getWidth();
    protected float h = Gdx.graphics.getHeight();

    public State(GameStateManager gsm, DataRepository dataRepository) {
        this.gsm = gsm;
        this.dataRepository = dataRepository;
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
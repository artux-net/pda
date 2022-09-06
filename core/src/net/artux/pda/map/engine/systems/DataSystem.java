package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.model.input.Map;
import net.artux.pda.model.user.UserModel;

public class DataSystem extends EntitySystem {

    private final Map map;
    private final UserModel userModel;

    public DataSystem(Map map, UserModel userModel) {
        this.map = map;
        this.userModel = userModel;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public Map getMap() {
        return map;
    }

    public UserModel getMember() {
        return userModel;
    }
}

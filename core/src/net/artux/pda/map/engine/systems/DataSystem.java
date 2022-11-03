package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.user.UserModel;

public class DataSystem extends EntitySystem {

    private final GameMap map;
    private final UserModel userModel;

    public DataSystem(GameMap map, UserModel userModel) {
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

    public GameMap getMap() {
        return map;
    }

    public UserModel getMember() {
        return userModel;
    }
}

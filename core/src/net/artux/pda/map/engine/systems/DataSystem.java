package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.model.input.Map;
import net.artux.pda.map.models.UserGdx;

public class DataSystem extends EntitySystem {

    private final Map map;
    private final UserGdx userModel;

    public DataSystem(Map map, UserGdx userModel) {
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

    public UserGdx getMember() {
        return userModel;
    }
}

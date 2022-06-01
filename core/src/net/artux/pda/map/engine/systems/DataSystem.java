package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.model.Map;
import net.artux.pdalib.Member;

public class DataSystem extends EntitySystem {

    private final Map map;
    private final Member member;

    public DataSystem(Map map, Member member) {
        this.map = map;
        this.member = member;
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

    public Member getMember() {
        return member;
    }
}

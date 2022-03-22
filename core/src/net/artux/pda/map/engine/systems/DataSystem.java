package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.model.Map;
import net.artux.pdalib.Member;

public class DataSystem extends EntitySystem {

    private Map map;
    private Member member;

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

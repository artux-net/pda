package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pdalib.Member;

public class LogSystem extends BaseSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pmm = ComponentMapper.getFor(PlayerComponent.class);

    public LogSystem() {
        super(null);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public Member getPlayerMember() {
        PlayerComponent playerComponent = pmm.get(player);
        return playerComponent.member;
    }

    public float getHealth() {
        HealthComponent healthComponent = hm.get(player);
        return healthComponent.value;
    }

    public Vector2 getPosition() {
        PositionComponent playerPosition = pm.get(player);
        return playerPosition.getPosition();
    }
}

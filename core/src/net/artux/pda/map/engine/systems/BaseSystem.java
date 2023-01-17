package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.player.PlayerComponent;

public abstract class BaseSystem extends IteratingSystem {

    private Entity player;
    private ComponentMapper<HealthComponent> healthMapper = ComponentMapper.getFor(HealthComponent.class);
    private final Family playerFamily = Family.all(PlayerComponent.class, Position.class).get();

    public BaseSystem(Family family) {
        super(family);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);
        if (players.size() > 0)
            player = players.first();
    }

    protected boolean isPlayerActive() {
        if (healthMapper.get(getPlayer()).isDead())
            return false;
        return getPlayer() != null;
    }

    protected Entity getPlayer() {
        if (player != null)
            return player;

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);
        if (players.size() > 0) {
            player = players.first();
            return player;
        } else
            return null;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (player == null) {
            ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);
            if (players.size() > 0)
                player = players.first();
        }
    }
}

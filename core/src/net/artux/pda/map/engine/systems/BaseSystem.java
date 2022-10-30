package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.artux.pda.map.engine.components.player.PlayerComponent;

public abstract class BaseSystem extends IteratingSystem {

    protected Entity player;

    public BaseSystem(Family family) {
        super(family);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
    }

    protected boolean isPlayerActive() {
        return getEngine().getEntities().contains(player, true);
    }

}

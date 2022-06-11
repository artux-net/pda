package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;

public class BaseSystem extends EntitySystem {

    protected Array<Entity> entities;
    protected Entity player;
    private final Family family;

    public BaseSystem(Family family) {
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get()).get(0);
        if (family!=null) {
            entities = new Array<>(engine.getEntitiesFor(family).toArray());

            engine.addEntityListener(family, new EntityListener() {
                @Override
                public void entityAdded(Entity entity) {
                    entities.add(entity);
                }

                @Override
                public void entityRemoved(Entity entity) {
                    entities.removeValue(entity, true);
                }
            });
        }
    }

    protected boolean isPlayerActive(){
        return getEngine().getEntities().contains(player, true);
    }

    public Array<Entity> listenEntities(Family family){
        final Array<Entity> entities = new Array<>(getEngine().getEntitiesFor(family).toArray());

        getEngine().addEntityListener(family, new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                entities.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                entities.removeValue(entity, true);
            }
        });
        return entities;
    }
}

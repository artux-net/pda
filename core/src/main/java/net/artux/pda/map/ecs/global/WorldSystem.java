package net.artux.pda.map.ecs.global;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.physics.BodyComponent;

import javax.inject.Inject;

@PerGameMap
public class WorldSystem extends EntitySystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final World world;

    @Inject
    public WorldSystem(World world) {
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(Family.all(BodyComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {

            }

            @Override
            public void entityRemoved(Entity entity) {
                BodyComponent bodyComponent = pm.get(entity);
                if (!bodyComponent.isDestroyed()) {
                    bodyComponent.setDestroyed(true);
                    world.destroyBody(bodyComponent.body);
                }
            }
        });
    }

    public void addEntity(Entity entity) {
        getEngine().addEntity(entity);
    }

    public void removeEntity(Entity entity) {
        getEngine().removeEntity(entity);
    }
}

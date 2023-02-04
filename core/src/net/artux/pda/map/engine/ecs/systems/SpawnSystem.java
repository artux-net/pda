package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.GroupComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.Position;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;

import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class SpawnSystem extends IteratingSystem {

    private ImmutableArray<Entity> spawns;
    private ImmutableArray<Entity> groupEntities;
    private final DataRepository dataRepository;

    private final ComponentMapper<SpawnComponent> sm = ComponentMapper.getFor(SpawnComponent.class);
    private final ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private final ComponentMapper<GroupComponent> gm = ComponentMapper.getFor(GroupComponent.class);

    @Inject
    public SpawnSystem(DataRepository dataRepository) {
        super(Family.all(VisionComponent.class, Position.class, VelocityComponent.class).exclude(PassivityComponent.class).get());
        this.dataRepository = dataRepository;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        spawns = engine.getEntitiesFor(
                Family
                        .all(SpawnComponent.class, Position.class)
                        .exclude(PassivityComponent.class, VelocityComponent.class)
                        .get());
        groupEntities = engine.getEntitiesFor(Family.all(GroupComponent.class, Position.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < spawns.size(); i++) {
            SpawnComponent spawnComponent = sm.get(spawns.get(i));
            Position spawnPosition = pm.get(spawns.get(i));

            List<Entity> entities = spawnComponent.getGroupComponent().getEntities();
            entities.removeIf(e -> getEntities().indexOf(e, true) < 0);
            if (spawnComponent.isEmpty()) {
                if (!spawnComponent.isActionsDone()) {
                    Gdx.app.log("Spawn actions", "Actions sent");
                    dataRepository.applyActions(spawnComponent.getSpawnModel().getActions());
                    spawnComponent.setActionsDone(true);
                }

                GroupComponent minDstGroup = null;
                float minDst = 50;
                for (Entity entity : groupEntities) {
                    GroupComponent group = gm.get(entity);
                    if (!(group.getTargeting() instanceof SpawnComponent)) {
                        float dst = group.getCenterPoint().dst(spawnPosition);
                        if (minDstGroup == null
                                || dst < minDst) {
                            minDstGroup = group;
                            minDst = dst;
                        }
                        spawnComponent.setGroup(group);
                    }
                }

            }
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public Entity getEmptySpawn() {
        for (Entity entity : spawns) {
            SpawnComponent spawnComponent = sm.get(entity);
            if (spawnComponent.isEmpty()) {
                return entity;
            }
        }
        return null;
    }

    public Entity getRandomSpawn() {
        return spawns.random();
    }
}

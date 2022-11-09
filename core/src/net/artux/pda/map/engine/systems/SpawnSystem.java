package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpawnComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;

import java.util.Timer;

public class SpawnSystem extends IteratingSystem implements Disposable {

    private ImmutableArray<Entity> spawns;
    private final DataRepository dataRepository;

    private final ComponentMapper<SpawnComponent> sm = ComponentMapper.getFor(SpawnComponent.class);

    private Timer timer;

    public SpawnSystem(DataRepository dataRepository) {
        super(Family.all(VisionComponent.class, PositionComponent.class, VelocityComponent.class).get());
        this.dataRepository = dataRepository;
        timer = new Timer();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        spawns = engine.getEntitiesFor(Family.all(SpawnComponent.class, PositionComponent.class).exclude(VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < spawns.size(); i++) {
            SpawnComponent spawnComponent = sm.get(spawns.get(i));

            spawnComponent.getEntities().removeIf(e -> getEntities().indexOf(e, true) < 0);
            if (spawnComponent.getEntities().size() < 1 && !spawnComponent.isActionsDone()) {
                Gdx.app.log("Spawn actions", "Actions sent");
                dataRepository.applyActions(spawnComponent.getSpawnModel().getActions());
                spawnComponent.setActionsDone(true);
            }
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    @Override
    public void dispose() {
        timer.cancel();
        timer.purge();
    }
}

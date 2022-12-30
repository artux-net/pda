package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.di.core.MapComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpawnComponent;
import net.artux.pda.map.engine.components.TransferComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.map.engine.systems.player.CameraSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.map.engine.systems.SpawnSystem;
import net.artux.pda.map.engine.systems.TimerSystem;

import java.util.Properties;

public class RandomSpawnerHelper {

    private static ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private static ComponentMapper<SpawnComponent> sm = ComponentMapper.getFor(SpawnComponent.class);

    public static void init(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        EntityProcessorSystem entityProcessorSystem = coreComponent.getEntityProcessor();
        EntityBuilder entityBuilder = coreComponent.getEntityBuilder();

        TimerSystem timerSystem = engine.getSystem(TimerSystem.class);
        CameraSystem cameraSystem = engine.getSystem(CameraSystem.class);
        SpawnSystem spawnSystem = engine.getSystem(SpawnSystem.class);
        MapOrientationSystem mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
        Properties properties = coreComponent.getDataRepository().getProperties();
        ImmutableArray<Entity> transfers = engine.getEntitiesFor(Family.all(PositionComponent.class, TransferComponent.class).get());
        ImmutableArray<Entity> spawns = engine.getEntitiesFor(Family.all(PositionComponent.class, SpawnComponent.class).get());

        float groupFreq = Float.parseFloat((String) properties.get(PropertyFields.GROUP_BOT_FREQ));
        timerSystem.addTimerAction(groupFreq, new TimerSystem.TimerListener() {
            @Override
            public void action() {
                Entity randomTransfer = transfers.random();
                Vector2 randomTransferPosition;
                if (randomTransfer == null)
                    randomTransferPosition = mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera());
                else
                    randomTransferPosition = pm.get(randomTransfer);

                Entity targetSpawn = spawnSystem.getEmptySpawn();
                if (targetSpawn != null) {
                    Vector2 spawnPosition = pm.get(targetSpawn);
                    entityProcessorSystem.generateTakeSpawnGroup(randomTransferPosition, spawnPosition);
                    return;
                }
                targetSpawn = spawnSystem.getRandomSpawn();
                if (targetSpawn != null)
                    entityProcessorSystem.generateAttackSpawnGroup(randomTransferPosition, sm.get(targetSpawn));
            }
        });

        float singleFreq = Float.parseFloat((String) properties.get(PropertyFields.SINGLE_BOT_FREQ));
        timerSystem.addTimerAction(singleFreq, () -> entityProcessorSystem
                .addEntity(entityBuilder.randomStalker(()
                -> mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera()))));
    }

}

package net.artux.pda.map.content;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.engine.ecs.components.map.TransferComponent;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem;
import net.artux.pda.map.engine.ecs.systems.SpawnSystem;
import net.artux.pda.map.engine.ecs.systems.TimerSystem;
import net.artux.pda.map.utils.di.components.MapComponent;

import java.util.Properties;

public class RandomSpawnerHelper {

    private static final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private static final ComponentMapper<SpawnComponent> sm = ComponentMapper.getFor(SpawnComponent.class);

    public static void init(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        EntityProcessorSystem entityProcessorSystem = coreComponent.getEntityProcessor();
        EntityBuilder entityBuilder = coreComponent.getEntityBuilder();

        TimerSystem timerSystem = engine.getSystem(TimerSystem.class);
        SpawnSystem spawnSystem = engine.getSystem(SpawnSystem.class);
        MapOrientationSystem mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
        Properties properties = coreComponent.getDataRepository().getProperties();
        ImmutableArray<Entity> transfers = engine.getEntitiesFor(Family.all(BodyComponent.class, TransferComponent.class).get());

        float groupFreq = Float.parseFloat((String) properties.get(PropertyFields.GROUP_BOT_FREQ));
        timerSystem.addTimerAction(groupFreq, () -> {
            Entity randomTransfer = transfers.random();
            Vector2 randomTransferPosition;
            if (randomTransfer == null)
                randomTransferPosition = mapOrientationSystem.getRandomFreePoint();
            else
                randomTransferPosition = pm.get(randomTransfer).getPosition();

            Entity targetSpawn = spawnSystem.getEmptySpawn();
            if (targetSpawn != null) {
                Vector2 spawnPosition = pm.get(targetSpawn).getPosition();
                entityProcessorSystem.generateTakeSpawnGroup(randomTransferPosition, spawnPosition);
                return;
            }
            targetSpawn = spawnSystem.getRandomSpawn();
            entityProcessorSystem.generateAttackSpawnGroup(randomTransferPosition, sm.get(targetSpawn));
        });

        float singleFreq = Float.parseFloat((String) properties.get(PropertyFields.SINGLE_BOT_FREQ));
        timerSystem.addTimerAction(singleFreq, () -> {
            for (int i = 0; i < random(4, 5); i++) {
                entityProcessorSystem
                        .addEntity(entityBuilder.randomMutant(mapOrientationSystem::getRandomFreePoint));
            }
        });
    }

}

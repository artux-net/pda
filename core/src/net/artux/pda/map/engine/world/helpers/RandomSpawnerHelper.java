package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpawnComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.TransferComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.CameraSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.map.engine.systems.TimerSystem;

import java.util.Properties;

public class RandomSpawnerHelper {

    private static ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public static void init(Engine engine, DataRepository dataRepository, EntityBuilder entityBuilder) {
        TimerSystem timerSystem = engine.getSystem(TimerSystem.class);
        CameraSystem cameraSystem = engine.getSystem(CameraSystem.class);
        MapOrientationSystem mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
        Properties properties = dataRepository.getProperties();
        ImmutableArray<Entity> transfers = engine.getEntitiesFor(Family.all(PositionComponent.class, TransferComponent.class).get());
        ImmutableArray<Entity> spawns = engine.getEntitiesFor(Family.all(PositionComponent.class, SpawnComponent.class).get());

        float groupFreq = Float.parseFloat((String) properties.get(PropertyFields.GROUP_BOT_FREQ));
        timerSystem.addTimerAction(groupFreq, new TimerSystem.TimerListener() {
            @Override
            public void action() {
                Vector2 randomTransferPosition = pm.get(transfers.random());
                if (randomTransferPosition != null)
                    entityBuilder.spawnAttackGroup(randomTransferPosition, new TargetMovingComponent.Targeting() {
                        @Override
                        public Vector2 getTarget() {
                            return null;
                        }
                    });
            }
        });

        float singleFreq = Float.parseFloat((String) properties.get(PropertyFields.SINGLE_BOT_FREQ));
        timerSystem.addTimerAction(singleFreq, () -> entityBuilder.randomStalker(()
                -> mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera())));
    }

}

package net.artux.pda.map.di.modules;

import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.engine.ecs.systems.ArtifactSystem;
import net.artux.pda.map.engine.ecs.systems.BattleSystem;
import net.artux.pda.map.engine.ecs.systems.DeadCheckerSystem;
import net.artux.pda.map.engine.ecs.systems.MapLoggerSystem;
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem;
import net.artux.pda.map.engine.ecs.systems.MovementTargetingSystem;
import net.artux.pda.map.engine.ecs.systems.MovingSystem;
import net.artux.pda.map.engine.ecs.systems.RenderSystem;
import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.engine.ecs.systems.SpawnSystem;
import net.artux.pda.map.engine.ecs.systems.statemachine.StatesSystem;
import net.artux.pda.map.engine.ecs.systems.TimerSystem;
import net.artux.pda.map.engine.ecs.systems.VisionSystem;
import net.artux.pda.map.engine.ecs.systems.WorldSystem;
import net.artux.pda.map.engine.ecs.systems.player.CameraSystem;
import net.artux.pda.map.engine.ecs.systems.player.ClicksSystem;
import net.artux.pda.map.engine.ecs.systems.player.FogSystem;
import net.artux.pda.map.engine.ecs.systems.player.InteractionSystem;
import net.artux.pda.map.engine.ecs.systems.player.MissionsSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerBattleSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerMovingSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module(includes = {GameStageModule.class})
public abstract class EngineSystemsModule {

    @IntoSet
    @Binds
    public abstract EntitySystem clicksSystem(ClicksSystem clicksSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem missionsSystem(MissionsSystem missionsSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem fogSystem(FogSystem fogSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem playerMovingSystem(PlayerMovingSystem playerMovingSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem playerBattleSystem(PlayerBattleSystem playerBattleSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem mapOrientationSystem(MapOrientationSystem mapOrientationSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem mapLoggerSystem(MapLoggerSystem mapLoggerSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem cameraSystem(CameraSystem cameraSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem soundsSystem(SoundsSystem soundsSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem interactionSystem(InteractionSystem interactionSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem playerSystem(PlayerSystem playerSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem worldSystem(WorldSystem worldSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem artifactSystem(ArtifactSystem artifactSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem renderSystem(RenderSystem renderSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem battleSystem(BattleSystem battleSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem statesSystem(StatesSystem statesSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem movementTargetingSystem(MovementTargetingSystem movementTargetingSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem visionSystem(VisionSystem visionSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem movingSystem(MovingSystem movingSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem deadCheckerSystem(DeadCheckerSystem deadCheckerSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem spawnSystem(SpawnSystem spawnSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem timerSystem(TimerSystem timerSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem EntityProcessor(EntityProcessorSystem EntityProcessorSystem);
}

package net.artux.pda.map.di.core;

import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.map.engine.systems.ArtifactSystem;
import net.artux.pda.map.engine.systems.BattleSystem;
import net.artux.pda.map.engine.systems.CameraSystem;
import net.artux.pda.map.engine.systems.ClicksSystem;
import net.artux.pda.map.engine.systems.DeadCheckerSystem;
import net.artux.pda.map.engine.systems.InteractionSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.map.engine.systems.MovementTargetingSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.engine.systems.SpawnSystem;
import net.artux.pda.map.engine.systems.StatesSystem;
import net.artux.pda.map.engine.systems.TimerSystem;
import net.artux.pda.map.engine.systems.VisionSystem;
import net.artux.pda.map.engine.systems.WorldSystem;

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
    public abstract EntitySystem mapOrientationSystem(MapOrientationSystem mapOrientationSystem);

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

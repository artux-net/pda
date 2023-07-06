package net.artux.pda.map.di.modules;

import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.ecs.anomaly.AnomalySystem;
import net.artux.pda.map.ecs.anomaly.ArtifactSystem;
import net.artux.pda.map.ecs.battle.BattleSystem;
import net.artux.pda.map.ecs.battle.DeadCheckerSystem;
import net.artux.pda.map.ecs.effects.EffectsSystem;
import net.artux.pda.map.ecs.effects.ejection.EjectionSystem;
import net.artux.pda.map.ecs.creation.EntityProcessorSystem;
import net.artux.pda.map.ecs.characteristics.HealthSystem;
import net.artux.pda.map.ecs.battle.InfightingSystem;
import net.artux.pda.map.ecs.global.WorldSystem;
import net.artux.pda.map.ecs.logger.MapLoggerSystem;
import net.artux.pda.map.ecs.ai.MapOrientationSystem;
import net.artux.pda.map.ecs.physics.MovementTargetingSystem;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.interactive.SecretSystem;
import net.artux.pda.map.ecs.sound.SoundsSystem;
import net.artux.pda.map.ecs.takeover.SpawnSystem;
import net.artux.pda.map.ecs.interactive.TimerSystem;
import net.artux.pda.map.ecs.vision.VisionSystem;
import net.artux.pda.map.ecs.camera.CameraSystem;
import net.artux.pda.map.ecs.interactive.ClicksSystem;
import net.artux.pda.map.ecs.vision.FogSystem;
import net.artux.pda.map.ecs.interactive.InteractionSystem;
import net.artux.pda.map.ecs.player.MissionsSystem;
import net.artux.pda.map.ecs.battle.PlayerBattleSystem;
import net.artux.pda.map.ecs.physics.PlayerMovingSystem;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.ecs.ai.statemachine.StatesSystem;

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
    public abstract EntitySystem infightingSystem(InfightingSystem infightingSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem missionsSystem(MissionsSystem missionsSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem healthSystem(HealthSystem healthSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem fogSystem(FogSystem fogSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem playerMovingSystem(PlayerMovingSystem playerMovingSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem ejectionSystem(EjectionSystem ejectionSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem secretSystem(SecretSystem secretSystem);

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
    public abstract EntitySystem anomalySystem(AnomalySystem anomalySystem);

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
    public abstract EntitySystem deadCheckerSystem(DeadCheckerSystem deadCheckerSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem spawnSystem(SpawnSystem spawnSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem timerSystem(TimerSystem timerSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem worldSystem(WorldSystem worldSystem);

    @IntoSet
    @Binds
    public abstract EntitySystem effectsSystem(EffectsSystem effectsSystem);
}

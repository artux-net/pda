package net.artux.pda.map.ecs.anomaly;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.interactive.ClickComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.render.SpriteComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.camera.CameraSystem;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.engine.entities.model.Anomaly;
import net.artux.pda.map.managers.notification.NotificationController;
import net.artux.pda.map.managers.notification.NotificationType;
import net.artux.pda.map.di.scope.PerGameMap;

import java.util.EnumMap;
import java.util.Random;

import javax.inject.Inject;

@PerGameMap
public class AnomalySystem extends EntitySystem {

    private final RenderSystem renderSystem;
    private ImmutableArray<Entity> anomalies;
    private ImmutableArray<Entity> entities;
    private final Random random = new Random();

    private final AssetManager assetManager;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<AnomalyComponent> am = ComponentMapper.getFor(AnomalyComponent.class);
    private final ComponentMapper<SpriteComponent> scm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private final CameraSystem cameraSystem;
    private final AudioSystem audioSystem;
    private final NotificationController notificationController;
    private final LocaleBundle localeBundle;
    private final Sound anomaly;

    private final EnumMap<Anomaly, Sound> anomaliesWorkSounds;

    public static boolean radiation = true;

    @Inject
    public AnomalySystem(AssetManager assetManager, CameraSystem cameraSystem,
                         AudioSystem audioSystem, NotificationController notificationController,
                         RenderSystem renderSystem, LocaleBundle localeBundle) {
        this.assetManager = assetManager;
        this.localeBundle = localeBundle;
        this.cameraSystem = cameraSystem;
        this.audioSystem = audioSystem;
        this.renderSystem = renderSystem;
        this.notificationController = notificationController;
        anomaliesWorkSounds = new EnumMap(Anomaly.class);
        for (Anomaly a : Anomaly.values()) {
            anomaliesWorkSounds.put(a, assetManager.get(a.getSoundId()));
        }
        anomaly = assetManager.get("audio/sounds/pda/d-beep.ogg", Sound.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        anomalies = engine.getEntitiesFor(Family.all(AnomalyComponent.class, BodyComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, BodyComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean playerNearAnomaly = false;
        boolean playerIsInAnomaly = false;
        AnomalyComponent playerAnomaly = null;

        for (int j = 0; j < anomalies.size(); j++) {
            Entity anomalyEntity = anomalies.get(j);
            BodyComponent anomalyBody = pm.get(anomalyEntity);
            AnomalyComponent anomaly = am.get(anomalyEntity);

            boolean somebodyIsInAnomaly = false;


            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                BodyComponent bodyComponent = pm.get(entity);
                HealthComponent healthComponent = hcm.get(entity);
                boolean player = pcm.has(entity);

                float dst = anomalyBody.getPosition().dst(bodyComponent.getPosition());
                // detection of anomalies with notification
                if (dst < anomaly.getSize() + 30f && player) {
                    playerNearAnomaly = true;

                    if (!scm.has(anomalyEntity)) {
                        float size = anomaly.getSize();
                        anomalyEntity
                                .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size * 2, size * 2))
                                .add(new ClickComponent((int) size,
                                        () -> renderSystem.showText(anomaly.desc(), anomalyBody.getPosition())));
                        notificationController.notify(NotificationType.ATTENTION,
                                localeBundle.get("main.anomaly.found"),
                                localeBundle.get("main.anomaly.found.desc", anomaly.getAnomaly().getTitle()));
                    }

                }

                if (dst > anomaly.getSize())
                    continue;

                somebodyIsInAnomaly = true;
                if (radiation)
                    healthComponent.radiationValue(0.006f);

                if (!player)
                    continue;

                if (random.nextDouble() > 0.999f) {
                    /*//soundsSystem.playSound();
                    Entity artifact = new Entity()
                            .add(new BodyComponent(getRandomAround(anomalyBody.getPosition(), anomaly.getSize()), world))
                            .add(new SpriteComponent(assetManager.get("yellow.png", Texture.class), 1, 1))
                            .add(new ArtifactComponent());
                    getEngine().addEntity(artifact);*/
                }
                playerIsInAnomaly = true;
                playerAnomaly = anomaly;

            }
            if (somebodyIsInAnomaly) {
                //delay act
                if (!anomaly.isScheduled()) {
                    int seconds = random(3, 5);
                    anomaly.setDelayedInteraction(
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
                                    audioSystem.playSoundAtDistance(anomaliesWorkSounds.get(anomaly.getAnomaly()), anomalyBody.getPosition(), 100);
                                    for (Entity e : entities) {
                                        BodyComponent bodyComponent = pm.get(e);
                                        if (anomalyBody.getPosition()
                                                .dst(bodyComponent.getPosition()) > anomaly.getSize())
                                            continue;
                                        anomaly.getAnomaly().interact(getEngine(), e);
                                    }

                                }
                            }, seconds)
                    );
                }
            } else if (anomaly.isScheduled())
                anomaly.getDelayedInteraction().cancel();
        }


        cameraSystem.setSpecialZoom(playerIsInAnomaly);
        if (playerIsInAnomaly) {
            timeToPlay -= deltaTime;
            if (timeToPlay < 0) {
                audioSystem.playSound(anomaly);
                long currentMillis = System.nanoTime() / 1000000;
                long milsDif = playerAnomaly.getTimeToActivate() - currentMillis;
                timeToPlay = milsDif / (1000f * 10);
            }
        } else if (playerNearAnomaly) {
            timeToPlay -= deltaTime;
            if (timeToPlay < 0) {
                audioSystem.playSound(anomaly);
                timeToPlay = 1f;
            }
        }
    }

    float timeToPlay = 0;

    /*public void playSound() {
        anomaly.play(VOLUME);
    }*/

    /*private void generateGroup() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                *//*TargetMovingComponent.Targeting targeting = new TargetMovingComponent.Targeting() {
                    @Override
                    public Vector2 getTarget() {
                        return mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera());
                    }
                };

                entityBuilder.randomStalker(targeting.getTarget(), targeting);
                Gdx.app.getApplicationLogger().log("WorldSystem", "New entity created.");
                generateGroup();*//*
                //todo
            }
        }, 1000 * random(40, 60));
    }*/
}

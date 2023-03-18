package net.artux.pda.map.engine.ecs.systems;

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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.AnomalyComponent;
import net.artux.pda.map.engine.ecs.components.ArtifactComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.engine.ecs.systems.player.CameraSystem;
import net.artux.pda.map.repository.RandomPosition;

import java.util.Random;

import javax.inject.Inject;

@PerGameMap
public class AnomalySystem extends EntitySystem {

    private ImmutableArray<Entity> anomalies;
    private ImmutableArray<Entity> entities;
    private Random random = new Random();

    private final AssetManager assetManager;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<AnomalyComponent> am = ComponentMapper.getFor(AnomalyComponent.class);

    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private final CameraSystem cameraSystem;
    private final SoundsSystem soundsSystem;
    private final World world;
    private Timer timer;
    private Sound anomaly;

    public static boolean radiation = true;

    @Inject
    public AnomalySystem(AssetManager assetManager, CameraSystem cameraSystem, SoundsSystem soundsSystem, World world) {
        this.assetManager = assetManager;
        this.world = world;
        timer = new Timer();
        anomaly = assetManager.get("audio/sounds/pda/d-beep.ogg", Sound.class);
        this.cameraSystem = cameraSystem;
        this.soundsSystem = soundsSystem;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        anomalies = engine.getEntitiesFor(Family.all(AnomalyComponent.class, BodyComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, BodyComponent.class).get());
        //generateGroup();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean playerIsInAnomaly = false;
        AnomalyComponent playerAnomaly = null;

        for (int j = 0; j < anomalies.size(); j++) {
            BodyComponent anomalyBody = pm.get(anomalies.get(j));
            AnomalyComponent anomaly = am.get(anomalies.get(j));

            boolean somebodyIsInAnomaly = false;
            for (int i = 0; i < entities.size(); i++) {
                BodyComponent bodyComponent = pm.get(entities.get(i));
                HealthComponent healthComponent = hcm.get(entities.get(i));

                //if (radiation)
                //  healthComponent.damage(healthComponent.radiation * deltaTime * 0.01f);

                if (anomalyBody.getPosition().dst(bodyComponent.getPosition()) > anomaly.getSize())
                    continue;

                somebodyIsInAnomaly = true;
                if (radiation)
                    healthComponent.radiation += 0.006;


                if (pcm.has(entities.get(i)))
                    if (random.nextDouble() > 0.999f) {
                        //soundsSystem.playSound();
                        Entity entity = new Entity()
                                .add(new BodyComponent(RandomPosition.getRandomAround(anomalyBody.getPosition(), anomaly.getSize()), world))
                                .add(new SpriteComponent(assetManager.get("yellow.png", Texture.class), 1, 1))
                                .add(new ArtifactComponent());
                        getEngine().addEntity(entity);
                    }

                if (!playerIsInAnomaly) {
                    playerIsInAnomaly = pcm.has(entities.get(i));
                    if (playerIsInAnomaly) {
                        playerAnomaly = anomaly;
                    }

                }
            }
            if (somebodyIsInAnomaly) {
                //delay act
                if (!anomaly.isScheduled()) {
                    int seconds = random(5, 15);
                    anomaly.setDelayedInteraction(
                            Timer.schedule(new Timer.Task() {
                                @Override
                                public void run() {
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
                anomaly.play();
                long currentMillis = System.nanoTime() / 1000000;
                long milsDif = playerAnomaly.getTimeToActivate() - currentMillis;
                timeToPlay = milsDif / (1000f * 10);
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
                Gdx.app.log("WorldSystem", "New entity created.");
                generateGroup();*//*
                //todo
            }
        }, 1000 * random(40, 60));
    }*/
}

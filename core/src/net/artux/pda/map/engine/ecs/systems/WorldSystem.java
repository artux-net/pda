package net.artux.pda.map.engine.ecs.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.repository.RandomPosition;
import net.artux.pda.map.engine.ecs.components.AnomalyComponent;
import net.artux.pda.map.engine.ecs.components.ArtifactComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.engine.ecs.systems.player.CameraSystem;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

@PerGameMap
public class WorldSystem extends EntitySystem implements Disposable {

    private ImmutableArray<Entity> anomalies;
    private ImmutableArray<Entity> entities;
    private Random random = new Random();
    private RandomPosition randomPosition = new RandomPosition();

    private final AssetManager assetManager;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<AnomalyComponent> am = ComponentMapper.getFor(AnomalyComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private final CameraSystem cameraSystem;
    private final SoundsSystem soundsSystem;
    private final World world;
    private Timer timer;
    public static boolean radiation = true;

    @Inject
    public WorldSystem(AssetManager assetManager, CameraSystem cameraSystem, SoundsSystem soundsSystem, World world) {
        this.assetManager = assetManager;
        this.world = world;
        timer = new Timer();
        this.cameraSystem = cameraSystem;
        this.soundsSystem = soundsSystem;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        anomalies = engine.getEntitiesFor(Family.all(AnomalyComponent.class, BodyComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, BodyComponent.class, VelocityComponent.class).get());
        generateGroup();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean player = false;
        for (int i = 0; i < entities.size(); i++) {
            BodyComponent bodyComponent = pm.get(entities.get(i));
            VelocityComponent velocityComponent = vm.get(entities.get(i));
            HealthComponent healthComponent = hcm.get(entities.get(i));
            if (radiation)
                healthComponent.damage(healthComponent.radiation * deltaTime * 0.01f);

            for (int j = 0; j < anomalies.size(); j++) {
                BodyComponent bodyComponent1 = pm.get(anomalies.get(j));
                AnomalyComponent anomalyComponent = am.get(anomalies.get(j));
                if (bodyComponent1.getPosition().dst(bodyComponent.getPosition()) < anomalyComponent.size) {
                    if (velocityComponent.len() > anomalyComponent.maxVelocity)
                        healthComponent.damage(anomalyComponent.damage);
                    if (radiation)
                        healthComponent.radiation += 0.006;

                    if (pcm.has(entities.get(i)))
                        if (random.nextDouble() > 0.999f) {
                            soundsSystem.playSound();
                            Entity entity = new Entity()
                                    .add(new BodyComponent(RandomPosition.getRandomAround(bodyComponent1.getPosition(), anomalyComponent.size), world))
                                    .add(new SpriteComponent(assetManager.get("yellow.png", Texture.class), 1, 1))
                                    .add(new ArtifactComponent());
                            getEngine().addEntity(entity);
                        }

                    if (!player)
                        player = pcm.has(entities.get(i));
                }
            }
        }
        cameraSystem.setSpecialZoom(player);

    }

    private void generateGroup() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                /*TargetMovingComponent.Targeting targeting = new TargetMovingComponent.Targeting() {
                    @Override
                    public Vector2 getTarget() {
                        return mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera());
                    }
                };

                entityBuilder.randomStalker(targeting.getTarget(), targeting);
                Gdx.app.log("WorldSystem", "New entity created.");
                generateGroup();*/
                //todo
            }
        }, 1000 * random(40, 60));
    }

    @Override
    public void dispose() {
        timer.cancel();
        timer.purge();
    }
}

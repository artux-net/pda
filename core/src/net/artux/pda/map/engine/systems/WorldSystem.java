package net.artux.pda.map.engine.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.RandomPosition;
import net.artux.pda.map.engine.components.AnomalyComponent;
import net.artux.pda.map.engine.components.ArtifactComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.states.BotStatesAshley;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WeaponModel;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WorldSystem extends EntitySystem implements Disposable {

    private ImmutableArray<Entity> anomalies;
    private ImmutableArray<Entity> entities;
    private Random random = new Random();
    private RandomPosition randomPosition = new RandomPosition();

    private final AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<AnomalyComponent> am = ComponentMapper.getFor(AnomalyComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private CameraSystem cameraSystem;
    private SoundsSystem soundsSystem;
    private MapOrientationSystem mapOrientationSystem;
    private Timer timer;

    public static boolean radiation = true;

    public WorldSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        timer = new Timer();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        anomalies = engine.getEntitiesFor(Family.all(AnomalyComponent.class, PositionComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, PositionComponent.class, VelocityComponent.class).get());
        cameraSystem = engine.getSystem(CameraSystem.class);
        soundsSystem = engine.getSystem(SoundsSystem.class);
        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
        generateGroup();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean player = false;
        for (int i = 0; i < entities.size(); i++) {
            PositionComponent positionComponent = pm.get(entities.get(i));
            VelocityComponent velocityComponent = vm.get(entities.get(i));
            HealthComponent healthComponent = hcm.get(entities.get(i));
            if (radiation)
                healthComponent.damage(healthComponent.radiation * deltaTime * 0.01f);

            for (int j = 0; j < anomalies.size(); j++) {
                PositionComponent positionComponent1 = pm.get(anomalies.get(j));
                AnomalyComponent anomalyComponent = am.get(anomalies.get(j));
                if (positionComponent1.getPosition().dst(positionComponent.getPosition()) < anomalyComponent.size) {
                    if (velocityComponent.velocity.len() > anomalyComponent.maxVelocity)
                        healthComponent.damage(anomalyComponent.damage);
                    if (radiation)
                        healthComponent.radiation += 0.006;

                    if (pcm.has(entities.get(i)))
                        if (random.nextDouble() > 0.999f) {
                            soundsSystem.playSound();
                            Entity entity = new Entity();
                            entity.add(new PositionComponent(randomPosition
                                    .getRandomAround(positionComponent1.getPosition(),
                                            anomalyComponent.size)));
                            entity.add(new SpriteComponent(assetManager.get("yellow.png", Texture.class), 1, 1));
                            entity.add(new ArtifactComponent());
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
                Entity entity = new Entity();

                ArmorModel armor = new ArmorModel();
                WeaponModel w = new WeaponModel();
                w.setSpeed(30);
                w.setDamage(1);
                w.setPrecision(1);
                w.setBulletQuantity(15);

                entity.add(new PositionComponent(mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera())))
                        .add(new SpriteComponent(assetManager.get("red.png", Texture.class), 8, 8))
                        .add(new VelocityComponent())
                        .add(new HealthComponent())
                        .add(new WeaponComponent(armor, w, w))
                        .add(new StalkerComponent("Мутант", new ArrayList<ItemModel>()))
                        .add(new StatesComponent<>(entity, BotStatesAshley.FIND_TARGET, BotStatesAshley.GUARDING))
                        .add(new MoodComponent(-1, null, true))
                        .add(new TargetMovingComponent(new TargetMovingComponent.Targeting() {
                            @Override
                            public Vector2 getTarget() {
                                return mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera());
                            }
                        }));
                getEngine().addEntity(entity);
                Gdx.app.log("WorldSystem", "New entity created.");
                generateGroup();
            }
        }, 1000 * random(40, 60));
    }

   /* public Vector2 getRandomFreePosition(){
        Vector2 position = new Vector2(random.nextInt(GlobalData.mapWidth), random.nextInt(GlobalData.mapHeight));
        if (mapOrientationSystem.isGraphActive())
            while (mapOrientationSystem.getWorldGraph().getTypeInPosition(position.x, position.y) == TILE_WALL
                    || cameraSystem.getCamera().frustum.pointInFrustum(position.x, position.y, 0))
                position = new Vector2(random.nextInt(GlobalData.mapWidth), random.nextInt(GlobalData.mapHeight));
            return position;
    }*/

    @Override
    public void dispose() {
        timer.cancel();
        timer.purge();
    }
}

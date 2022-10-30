package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.RandomPosition;
import net.artux.pda.map.engine.components.AnomalyComponent;
import net.artux.pda.map.engine.components.ArtifactComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;

import java.util.Random;
import java.util.Timer;

public class WorldSystem extends EntitySystem implements Disposable, Drawable {

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
    private MapOrientationSystem mapOrientationSystem;
    private Timer timer;
    private EntityBuilder entityBuilder;
    public static boolean radiation = true;
    private final World world;

    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();


    public WorldSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        timer = new Timer();
        world = new World(new Vector2(0, 0), true);

    }

    public World getWorld() {
        return world;
    }

    public EntityBuilder getEntityBuilder() {
        return entityBuilder;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entityBuilder = new EntityBuilder(assetManager, engine, world);
        anomalies = engine.getEntitiesFor(Family.all(AnomalyComponent.class, PositionComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, PositionComponent.class, VelocityComponent.class).get());

        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);

       /* BodyDef groundBodyDef = new BodyDef();
// Set its world position
        groundBodyDef.position.set(new Vector2());

// Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

// Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(mapOrientationSystem.getMapBorders().getWidth(), mapOrientationSystem.getMapBorders().getHeight());
// Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f);
// Clean up after ourselves
        groundBox.dispose();*/

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        generateGroup();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean player = false;
        world.step(deltaTime, 3, 3);
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
                    if (velocityComponent.len() > anomalyComponent.maxVelocity)
                        healthComponent.damage(anomalyComponent.damage);
                    if (radiation)
                        healthComponent.radiation += 0.006;

                    if (pcm.has(entities.get(i)))
                        if (random.nextDouble() > 0.999f) {
                            getEngine().getSystem(SoundsSystem.class).playSound();
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
        cameraSystem = getEngine().getSystem(CameraSystem.class);


        cameraSystem.setSpecialZoom(player);

    }

    private void generateGroup() {
        /*timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TargetMovingComponent.Targeting targeting = new TargetMovingComponent.Targeting() {
                    @Override
                    public Vector2 getTarget() {
                        return mapOrientationSystem.getRandomFreePoint(cameraSystem.getCamera());
                    }
                };

                entityBuilder.randomStalker(targeting.getTarget(), targeting);
                Gdx.app.log("WorldSystem", "New entity created.");
                generateGroup();
            }
        }, 1000 * random(40, 60));*/
    }

    @Override
    public void dispose() {
        timer.cancel();
        timer.purge();
        debugRenderer.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        debugRenderer.render(world, cameraSystem.getCamera().combined);
    }
}

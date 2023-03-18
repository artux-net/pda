package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;

import javax.inject.Inject;

@PerGameMap
public class VisionSystem extends BaseSystem implements Drawable {

    private static final float VISION_DISTANCE = 150f;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private final MapOrientationSystem mapOrientationSystem;
    private final Sprite enemyTarget;
    private final World world;

    @Inject
    public VisionSystem(AssetManager assetManager, MapOrientationSystem mapOrientationSystem, World world) {
        super(Family.all(VisionComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
        this.world = world;

        enemyTarget = new Sprite(assetManager.get("transfer.png", Texture.class));
        enemyTarget.setSize(16, 16);
        enemyTarget.setOriginCenter();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent bodyComponentComponent1 = pm.get(entity);
        VisionComponent visionComponent1 = vcm.get(entity);
        visionComponent1.clear();

        ImmutableArray<Entity> entities = getEntities();
        for (int j = 0; j < entities.size(); j++) {
            Entity entity2 = entities.get(j);
            if (entity2 == entity)
                continue;

            BodyComponent bodyComponentComponent2 = pm.get(entity2);
            float dst = bodyComponentComponent1.getPosition().dst(bodyComponentComponent2.getPosition());

            if (dst < VISION_DISTANCE
                    && !mapOrientationSystem.collides(bodyComponentComponent1.getPosition(), bodyComponentComponent2.getPosition()))
                world.rayCast(new RayCastCallback() {
                    @Override
                    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                        if (fixture.getBody().getType() == BodyDef.BodyType.DynamicBody)
                            visionComponent1.addVisibleEntity(entity2);
                        return 0;
                    }
                }, bodyComponentComponent1.getPosition(), bodyComponentComponent2.getPosition());


        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Sprite sprite = enemyTarget;
        VisionComponent visionComponent = vcm.get(getPlayer());
        MoodComponent moodComponent = mm.get(getPlayer());
        Entity playerEnemyTarget = moodComponent.getEnemy();
        if (playerEnemyTarget != null) {
            if (visionComponent.getVisibleEntities().contains(playerEnemyTarget)) {
                Vector2 enemyPosition = pm.get(playerEnemyTarget).getPosition();
                batch.draw(sprite, enemyPosition.x - sprite.getOriginX(), enemyPosition.y - sprite.getOriginY(), sprite.getOriginX(),
                        sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), enemyTarget.getRotation());

                enemyTarget.rotate(-1.5f);
                enemyTarget.draw(batch);
            } else {
                moodComponent.setEnemy(null);
            }
        }
    }
}
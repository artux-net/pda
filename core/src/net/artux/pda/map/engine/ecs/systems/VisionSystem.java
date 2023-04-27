package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.view.bars.Bar;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

@PerGameMap
public class VisionSystem extends BaseSystem implements Drawable {

    private static final float VISION_DISTANCE = 150f;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private final MapOrientationSystem mapOrientationSystem;
    private final Sprite enemyTarget;
    private final World world;
    private final Bar progressBar;

    @Inject
    public VisionSystem(AssetManager assetManager, MapOrientationSystem mapOrientationSystem, World world, Skin skin) {
        super(Family.all(VisionComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
        this.world = world;

        enemyTarget = new Sprite(assetManager.get("transfer.png", Texture.class));
        enemyTarget.setSize(16, 16);
        enemyTarget.setOriginCenter();

        progressBar = new Bar(Color.GREEN);
        progressBar.setWidth(26);
        progressBar.setHeight(5);
    }

    AtomicBoolean wall = new AtomicBoolean(false);

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
                    && !mapOrientationSystem.collides(bodyComponentComponent1.getPosition(), bodyComponentComponent2.getPosition())) {
                wall.set(false);
                world.rayCast((fixture, point, normal, fraction) -> {
                    if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody)
                        wall.set(true);
                    return 1;
                }, bodyComponentComponent2.getPosition(), bodyComponentComponent1.getPosition());
                if (!wall.get())
                    visionComponent1.addVisibleEntity(entity2);
            }
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Sprite sprite = enemyTarget;
        VisionComponent visionComponent = vcm.get(getPlayer());
        MoodComponent moodComponent = mm.get(getPlayer());
        Entity playerEnemy = moodComponent.getEnemy();
        if (playerEnemy != null) {
            if (visionComponent.getVisibleEntities().contains(playerEnemy)) {
                Vector2 enemyPosition = pm.get(playerEnemy).getPosition();
                if (hcm.has(playerEnemy)) {
                    progressBar.setValue(hcm.get(playerEnemy).getHealth());
                    progressBar.setPosition(enemyPosition.x - 13, enemyPosition.y + 8);
                    progressBar.draw(batch, 1f);
                }

                batch.draw(sprite, enemyPosition.x - sprite.getOriginX(), enemyPosition.y - sprite.getOriginY(), sprite.getOriginX(),
                        sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), enemyTarget.getRotation());

                enemyTarget.rotate(-1.5f);
                enemyTarget.draw(batch);
            }
        }
    }
}
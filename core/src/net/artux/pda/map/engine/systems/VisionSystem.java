package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.VisionComponent;

import javax.inject.Inject;

@PerGameMap
public class VisionSystem extends BaseSystem implements Drawable {

    private static final float VISION_DISTANCE = 150f;

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private final MapOrientationSystem mapOrientationSystem;
    private final Sprite enemyTarget;

    @Inject
    public VisionSystem(AssetManager assetManager, MapOrientationSystem mapOrientationSystem) {
        super(Family.all(VisionComponent.class, Position.class).exclude(PassivityComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
        enemyTarget = new Sprite(assetManager.get("transfer.png", Texture.class));
        enemyTarget.setSize(16, 16);
        enemyTarget.setOriginCenter();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Position positionComponent1 = pm.get(entity);
        VisionComponent visionComponent1 = vcm.get(entity);
        visionComponent1.clear();

        ImmutableArray<Entity> entities = getEntities();
        for (int j = 0; j < entities.size(); j++) {
            Entity entity2 = entities.get(j);
            if (entity2 == entity)
                continue;

            Position positionComponent2 = pm.get(entity2);
            float dst = positionComponent1.dst(positionComponent2);

            if (dst < VISION_DISTANCE
                    && !mapOrientationSystem.collides(positionComponent1, positionComponent2)) {
                visionComponent1.addVisibleEntity(entity2);
            }
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
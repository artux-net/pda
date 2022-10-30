package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.artux.pda.map.engine.components.BodyComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.VisionComponent;

import java.util.Iterator;
import java.util.LinkedList;

public class VisionSystem extends BaseSystem implements Drawable {

    private static final float VISION_DISTANCE = 100f;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private InteractionSystem interactionSystem;
    private MapOrientationSystem mapOrientationSystem;
    private Sprite enemyTarget;

    public VisionSystem(AssetManager assetManager) {
        super(Family.all(VisionComponent.class, BodyComponent.class).get());
        enemyTarget = new Sprite(assetManager.get("transfer.png", Texture.class));
        enemyTarget.setSize(16, 16);
        enemyTarget.setOriginCenter();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        interactionSystem = engine.getSystem(InteractionSystem.class);
        interactionSystem.addButton("ui/icons/icon_target.png", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LinkedList<Entity> playerEnemies = vcm.get(player).getVisibleEntities();
                MoodComponent moodComponent = mm.get(player);
                Entity playerEnemyTarget = moodComponent.getEnemy();
                if (playerEnemies.size() > 1) {
                    Iterator<Entity> iterator = playerEnemies.iterator();
                    if (playerEnemyTarget == playerEnemies.getLast() ||
                            playerEnemyTarget == null || !playerEnemies.contains(playerEnemyTarget))
                        moodComponent.setEnemy(playerEnemies.getFirst());
                    else while (iterator.hasNext()) {
                        if (iterator.next() == playerEnemyTarget) {
                            moodComponent.setEnemy(iterator.next());
                            break;
                        }
                    }
                } else if (playerEnemies.size() == 1)
                    moodComponent.setEnemy(playerEnemies.getFirst());
            }
        });
        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        ImmutableArray<Entity> entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity1 = entities.get(i);

            Vector2 positionComponent1 = pm.get(entity1).getBody().getPosition();
            VisionComponent visionComponent1 = vcm.get(entity1);
            visionComponent1.clear();

            for (int j = 0; j < entities.size(); j++) {
                Entity entity2 = entities.get(j);
                if (entity2 == entity1)
                    continue;

                Vector2 positionComponent2 = pm.get(entity2).getBody().getPosition();
                float dst = positionComponent1.dst(positionComponent2);

                if (dst < VISION_DISTANCE && !mapOrientationSystem.collides(positionComponent1, positionComponent2)) {
                    visionComponent1.addVisibleEntity(entity2);
                }
            }
        }

        LinkedList<Entity> playerEnemies = vcm.get(player).getVisibleEntities();
        MoodComponent moodComponent = mm.get(player);
        if (moodComponent.hasEnemy() && !playerEnemies.contains(moodComponent.getEnemy()))
            moodComponent.setEnemy(null);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Sprite sprite = enemyTarget;
        VisionComponent visionComponent = vcm.get(player);
        MoodComponent moodComponent = mm.get(player);
        Entity playerEnemyTarget = moodComponent.getEnemy();
        if (playerEnemyTarget != null) {
            if (visionComponent.getVisibleEntities().contains(playerEnemyTarget)) {
                Vector2 enemyPosition = pm.get(playerEnemyTarget).getBody().getPosition();
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
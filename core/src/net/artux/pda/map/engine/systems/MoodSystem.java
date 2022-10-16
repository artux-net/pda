package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;

import java.util.Iterator;
import java.util.LinkedList;

public class MoodSystem extends BaseSystem implements Drawable {

    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> scm = ComponentMapper.getFor(SpriteComponent.class);
    private LinkedList<Entity> playerEnemies = new LinkedList<>();
    private InteractionSystem interactionSystem;
    private Sprite enemyTarget;
    private Entity playerEnemyTarget;

    public MoodSystem(AssetManager assetManager) {
        super(Family.all(MoodComponent.class, PositionComponent.class).get());
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
                if (playerEnemies.size() > 1) {
                    Iterator<Entity> iterator = playerEnemies.iterator();
                    if (playerEnemyTarget == playerEnemies.getLast() ||
                            playerEnemyTarget == null || !playerEnemies.contains(playerEnemyTarget))
                        playerEnemyTarget = playerEnemies.getFirst();
                    else while (iterator.hasNext()) {
                        if (iterator.next() == playerEnemyTarget) {
                            playerEnemyTarget = iterator.next();
                            break;
                        }
                    }
                } else
                    playerEnemyTarget = null;
            }
        });
    }

    public Entity getPlayerEnemyTarget() {
        return playerEnemyTarget;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        playerEnemies.clear();
        for (int i = 0; i < entities.size - 1; i++) {
            Entity entity1 = entities.get(i);

            MoodComponent moodComponent1 = mm.get(entity1);

            for (int j = i + 1; j < entities.size; j++) {
                Entity entity2 = entities.get(j);

                float dst = pm.get(entity1).getPosition().dst(pm.get(entity2).getPosition());
                MoodComponent moodComponent2 = mm.get(entity2);

                if (dst < 60) {
                    if (moodComponent1.isEnemy(moodComponent2)) {
                        if (moodComponent1.enemy == null) {
                            moodComponent1.setEnemy(entity2);
                        }
                    }
                }
                if (player == entity1) {
                    // select visible enemies for player
                    SpriteComponent spriteComponent = scm.get(entity2);
                    if (spriteComponent.isVisible())
                        playerEnemies.add(entity2);
                }


                if (moodComponent1.enemy == entity2)
                    moodComponent2.setEnemy(entity1);
            }

            if (moodComponent1.enemy != null && pm.get(entity1).getPosition().dst(pm.get(moodComponent1.enemy).getPosition()) > 100)
                moodComponent1.setEnemy(null);
        }
        if (playerEnemyTarget != null && !playerEnemies.contains(playerEnemyTarget))
            playerEnemyTarget = null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Sprite sprite = enemyTarget;
        MoodComponent moodComponent = mm.get(player);
        if (playerEnemyTarget != null) {
            if (getEngine().getEntities().contains(playerEnemyTarget, true)) {
                Vector2 enemyPosition = pm.get(playerEnemyTarget).getPosition();
                batch.draw(sprite, enemyPosition.x - sprite.getOriginX(), enemyPosition.y - sprite.getOriginY(), sprite.getOriginX(),
                        sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), enemyTarget.getRotation());

                enemyTarget.rotate(-1.5f);
                enemyTarget.draw(batch);
            } else {
                moodComponent.enemy = null; // TODO here null?
            }
        }
    }
}

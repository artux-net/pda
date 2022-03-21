package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.ui.UserInterface;

import java.util.Random;

public class BattleSystem extends EntitySystem implements Disposable {

    private Array<Entity> entities;
    private Batch batch;
    private SoundsSystem soundsSystem;
    private AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private ShapeRenderer sr = new ShapeRenderer();
    private Random random = new Random();

    private MapOrientationSystem mapOrientationSystem;

    public BattleSystem(AssetManager assetManager, Batch batch) {
        this.batch = batch;
        this.assetManager = assetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = new Array<>(engine.getEntitiesFor(Family.all(HealthComponent.class, PositionComponent.class, WeaponComponent.class).get()).toArray());

        engine.addEntityListener(Family.all(HealthComponent.class, PositionComponent.class, WeaponComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                entities.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                entities.removeValue(entity, true);
            }
        });

        soundsSystem = engine.getSystem(SoundsSystem.class);
        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            HealthComponent healthComponent = hm.get(entity);
            PositionComponent positionComponent = pm.get(entity);
            if (healthComponent.isDead()) {
                Entity deadEntity = new Entity();
                deadEntity.add(new PositionComponent(positionComponent.getPosition()))
                        .add(new SpriteComponent(assetManager.get("gray.png", Texture.class), 4, 4));//TODO

                getEngine().removeEntity(entity);
                getEngine().addEntity(deadEntity);
            }
        }
    }

    public void drawObjects(float delta) {
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            PositionComponent entityPosition = pm.get(entity);
            WeaponComponent entityWeapon = wm.get(entity);
            entityWeapon.update(delta);

            if (pcm.has(entity) && entityWeapon.getSelected() != null) {
                PlayerData.selectedWeapon = entityWeapon.getSelected().title;
                if (entityWeapon.resource != null) {
                    PlayerData.bullet = entityWeapon.resource.title;
                    PlayerData.resource = entityWeapon.resource.quantity;
                    PlayerData.magazine = entityWeapon.getMagazine();
                }
                //UserInterface.getLogger().put("Reloading", entityWeapon.reloading, "toString");
            }

            MoodComponent moodComponent = mm.get(entity);

            Entity enemy = moodComponent.enemy;

            if (enemy != null && pm.has(enemy)) {
                PositionComponent enemyPosition = pm.get(enemy);
                HealthComponent enemyHealth = hm.get(enemy);


                if (mapOrientationSystem.isGraphActive()) {
                    FlatTiledNode entityNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(entityPosition.getPosition());
                    FlatTiledNode enemyNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(enemyPosition.getPosition());
                    if (!mapOrientationSystem.collisionDetector.collides(new Ray<>(new Vector2(entityNode.x, entityNode.y), new Vector2(enemyNode.x, enemyNode.y)))) {
                        if (entityWeapon.getSelected() != null) {
                            if (entityWeapon.shoot()) {
                                sr.setColor(Color.ORANGE);
                                sr.setProjectionMatrix(batch.getProjectionMatrix());

                                sr.begin(ShapeRenderer.ShapeType.Filled);

                                Vector2 diff = enemyPosition.getPosition().cpy().sub(entityPosition.getPosition());
                                Vector2 delayed = entityPosition.getPosition().cpy().add(diff.scl(0.3f));


                                sr.rectLine(delayed,
                                        getPointNear(enemyPosition, entityWeapon.getSelected().precision), 1);
                                sr.end();
                                enemyHealth.value -= entityWeapon.getSelected().damage;
                                soundsSystem.playShoot(entityPosition.getPosition());
                                if (enemyHealth.isDead())
                                    moodComponent.setEnemy(null);
                            }
                        }
                    }
                }else if (entityWeapon.getSelected() != null) {
                    if (entityWeapon.shoot()) {
                        sr.setColor(Color.ORANGE);
                        sr.setProjectionMatrix(batch.getProjectionMatrix());

                        sr.begin(ShapeRenderer.ShapeType.Filled);

                        Vector2 diff = enemyPosition.getPosition().cpy().sub(entityPosition.getPosition());
                        Vector2 delayed = entityPosition.getPosition().cpy().add(diff.scl(0.3f));


                        sr.rectLine(delayed,
                                getPointNear(enemyPosition, entityWeapon.getSelected().precision), 1);
                        sr.end();
                        enemyHealth.value -= entityWeapon.getSelected().damage;
                        soundsSystem.playShoot(entityPosition.getPosition());
                        if (enemyHealth.isDead())
                            moodComponent.setEnemy(null);
                    }
                }
            }
        }

    }


    public Vector2 getPointNear(PositionComponent positionComponent, float precision) {
        double r = 5 / precision;

        double angle = random.nextInt(360);

        Vector2 basePosition = positionComponent.getPosition();
        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x + x, basePosition.y + y);
    }

    @Override
    public void dispose() {
        sr.dispose();
    }
}

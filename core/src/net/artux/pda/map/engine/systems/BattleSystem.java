package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
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

    public BattleSystem(AssetManager assetManager, Batch batch, SoundsSystem soundsSystem) {
        this.batch = batch;
        this.assetManager = assetManager;
        this.soundsSystem = soundsSystem;
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
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            HealthComponent healthComponent = hm.get(entity);
            PositionComponent positionComponent = pm.get(entity);
            if (healthComponent.isDead()){
                Entity deadEntity = new Entity();
                deadEntity.add(new PositionComponent(positionComponent.getPosition()))
                        .add(new SpriteComponent(assetManager.get("gray.png", Texture.class), 4,4));//TODO

                getEngine().removeEntity(entity);
                getEngine().addEntity(deadEntity);
            }
        }
    }

    public void drawObjects(float delta) {
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            PositionComponent positionComponent = pm.get(entity);
            WeaponComponent weaponComponent = wm.get(entity);
            weaponComponent.update(delta);
            if (pcm.has(entity) && weaponComponent.getSelected()!=null){
                UserInterface.getLogger().addRow("Main weapon " + weaponComponent.getSelected().title);
                UserInterface.getLogger().addRow("Reloading: " + weaponComponent.reloading);
                if (weaponComponent.resource!=null)
                    UserInterface.getLogger().addRow("Bullet: " + weaponComponent.resource.title +", " + weaponComponent.getMagazine() + "/" + weaponComponent.resource.quantity);
                else
                    UserInterface.getLogger().addRow("Bullet: " + "отсутствует");
            }

            MoodComponent moodComponent = mm.get(entity);

            Entity enemy = moodComponent.enemy;

            if (enemy != null && pm.has(enemy)) {
                PositionComponent positionComponent1 = pm.get(enemy);
                HealthComponent healthComponent1 = hm.get(enemy);

                if (weaponComponent.getSelected()!= null) {
                    if (weaponComponent.shoot()) {
                        sr.setColor(Color.ORANGE);
                        sr.setProjectionMatrix(batch.getProjectionMatrix());

                        sr.begin(ShapeRenderer.ShapeType.Filled);

                        Vector2 diff = positionComponent1.getPosition().cpy().sub(positionComponent.getPosition());
                        Vector2 delayed = positionComponent.getPosition().cpy().add(diff.scl(0.3f));


                        sr.rectLine(delayed,
                                getPointNear(positionComponent1, weaponComponent.getSelected().precision), 1);
                        sr.end();
                        healthComponent1.value -= weaponComponent.getSelected().damage;
                        soundsSystem.playShoot(positionComponent.getPosition());
                        if (healthComponent1.isDead())
                            moodComponent.setEnemy(null);
                    }
                }
            }

        }
    }

    public Vector2 getPointNear(PositionComponent positionComponent, float precision) {
        double r = 5/precision;

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

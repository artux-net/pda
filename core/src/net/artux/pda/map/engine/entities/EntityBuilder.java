package net.artux.pda.map.engine.entities;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ContentGenerator;
import net.artux.pda.map.engine.MessagingCodes;
import net.artux.pda.map.engine.components.BulletComponent;
import net.artux.pda.map.engine.components.FogOfWarComponent;
import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.components.GroupComponent;
import net.artux.pda.map.engine.components.GroupTargetMovingComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.states.StalkerState;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;

import java.util.ArrayList;

import javax.inject.Inject;

@PerGameMap
public class EntityBuilder {

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    private final AssetManager assetManager;
    private final ContentGenerator contentGenerator;
    private final Texture bulletTexture;

    @Inject
    public EntityBuilder(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.contentGenerator = new ContentGenerator();

        bulletTexture = assetManager.get("bullet.png", Texture.class);
    }

    public Entity player(Vector2 position, DataRepository dataRepository) {
        return new Entity()
                .add(new Position(position))
                .add(new VelocityComponent())
                .add(new VisionComponent())
                .add(new SpriteComponent(assetManager.get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(dataRepository, assetManager))
                .add(new MoodComponent(dataRepository.getCurrentStoryDataModel()))
                .add(new HealthComponent())
                .add(new PlayerComponent(dataRepository.getCurrentStoryDataModel()));
    }

    public Entity bullet(Entity author, Entity target, WeaponModel weaponModel) {
        Position position = pm.get(author);
        Vector2 targetPosition = pm.get(target);
        targetPosition = getPointNear(targetPosition.cpy(), weaponModel.getPrecision());

        float targetX = targetPosition.x;
        float targetY = targetPosition.y;

        float vX = (float) ((targetX - position.x) /
                Math.sqrt(((targetX - position.x) * (targetX - position.x)) + ((targetY - position.y) * (targetY - position.y))));
        float vY = (float) ((targetY - position.y) /
                Math.sqrt(((targetY - position.y) * (targetY - position.y)) + ((targetX - position.x) * (targetX - position.x))));

        vX *= weaponModel.getSpeed();
        vY *= weaponModel.getSpeed();

        Vector2 direction = targetPosition.cpy().sub(position);
        float degrees = (float) (Math.atan2(
                -direction.x,
                direction.y
        ) * 180.0d / Math.PI);

        SpriteComponent spriteComponent = new SpriteComponent(bulletTexture, 15, 2);
        spriteComponent.setRotation(degrees + 90);

        return new Entity()
                .add(new Position(position))
                .add(new VelocityComponent(vX, vY, true))
                .add(spriteComponent)
                .add(new FogOfWarComponent())
                .add(new BulletComponent(author, target, targetPosition, weaponModel.getDamage()));
    }

    public Vector2 getPointNear(Vector2 basePosition, float precision) {
        double r = 10 / precision;

        double angle = random.nextInt(360);

        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x + x, basePosition.y + y);
    }


    public Entity spawnStalker(Vector2 position, GroupComponent group) {
        Entity entity = new Entity();

        WeaponModel w = new WeaponModel();
        w.setType(ItemType.RIFLE);
        w.setSpeed(random(5, 10));
        w.setDamage(random(3, 5));
        w.setPrecision(random(15, 25));
        w.setBulletQuantity(30);

        HealthComponent healthComponent = new HealthComponent();
        healthComponent.setImmortal(group.getParams().contains("immortal"));

        StatesComponent statesComponent = new StatesComponent(entity, group.getDispatcher(), StalkerState.INITIAL, StalkerState.GUARDING);
        group.getDispatcher().addListener(statesComponent, MessagingCodes.ATTACKED);

        entity
                .add(new Position(position))
                .add(new GraphMotionComponent(null))
                .add(new VelocityComponent())
                .add(new VisionComponent())
                .add(healthComponent)
                .add(group)
                .add(group.getMood())
                .add(new StalkerComponent(contentGenerator.generateName(), new ArrayList<>()))
                .add(new WeaponComponent(w, assetManager))
                .add(statesComponent)
                .add(new GroupTargetMovingComponent(group))
                .add(new FogOfWarComponent());

        return entity;
    }

    public Entity randomStalker(GroupTargetMovingComponent.Targeting targeting) {
        Entity entity = new Entity();
/*

        WeaponModel w = new WeaponModel();
        w.setSpeed(random(5, 10));
        w.setDamage(random(3, 5));
        w.setPrecision(random(15, 25));
        w.setBulletQuantity(30);

        StatesComponent statesComponent = new StatesComponent(entity, null, StalkerState.INITIAL, StalkerState.GUARDING);

        entity.add(new PositionComponent(targeting.getTarget()))
                .add(new VisionComponent())
                .add(new RelationalSpriteComponent(8, 8))
                .add(new VelocityComponent())
                .add(new HealthComponent())
                .add(new GraphMotionComponent(null))
                .add(new WeaponComponent(w, this))
                .add(new StalkerComponent("Мутант", new ArrayList<ItemModel>()))
                .add(statesComponent)
                .add(new MoodComponent(-1, null, Collections.singleton("angry")))
                .add(new GroupTargetMovingComponent(targeting));
*/

        Gdx.app.log("WorldSystem", "New entity created.");
        return entity;
    }


}
package net.artux.pda.map.engine.entities;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.ContentGenerator;
import net.artux.pda.map.engine.components.BulletComponent;
import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.RelationalSpriteComponent;
import net.artux.pda.map.engine.components.SpawnComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.model.MobType;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.util.ArrayList;

public class EntityBuilder {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    private final AssetManager assetManager;
    private final ContentGenerator contentGenerator;
    private final Texture bulletTexture;
    private final Engine engine;
    private final MessageManager messageManager;

    public EntityBuilder(AssetManager assetManager, Engine engine) {
        this.assetManager = assetManager;
        this.engine = engine;
        this.contentGenerator = new ContentGenerator();
        this.messageManager = MessageManager.getInstance();
        bulletTexture = assetManager.get("bullet.png", Texture.class);
    }

    public Entity player(Vector2 position, StoryDataModel gdxData, UserModel userModel) {
        Entity player = new Entity();

        player.add(new PositionComponent(position))
                .add(new VelocityComponent())
                .add(new VisionComponent())
                .add(new SpriteComponent(assetManager.get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(gdxData, this))
                .add(new MoodComponent(userModel))
                .add(new HealthComponent())
                .add(new UserVelocityInput())
                .add(new PlayerComponent(userModel, gdxData));
        return player;
    }

    public Entity bullet(Entity author, Vector2 targetPosition, WeaponModel weaponModel) {
        PositionComponent position = pm.get(author);

        Entity player = new Entity();

        targetPosition = getPointNear(targetPosition, weaponModel.getPrecision());

        float targetX = targetPosition.x;
        float targetY = targetPosition.y;
        float vX = (float) ((targetX - position.x) /
                Math.sqrt(((targetX - position.x) * (targetX - position.x)) + ((targetY - position.y) * (targetY - position.y))));
        float vY = (float) ((targetY - position.y) /
                Math.sqrt(((targetY - position.y) * (targetY - position.y)) + ((targetX - position.x) * (targetX - position.x))));

        vX *= weaponModel.getSpeed() * 2;
        vY *= weaponModel.getSpeed() * 2;

        Vector2 direction = targetPosition.cpy().sub(position);
        float degrees = (float) (Math.atan2(
                -direction.x,
                direction.y
        ) * 180.0d / Math.PI);

        SpriteComponent spriteComponent = new SpriteComponent(bulletTexture, 15, 2);
        spriteComponent.setRotation(degrees + 90);

        player.add(new PositionComponent(position))
                .add(new VelocityComponent(vX, vY))
                .add(spriteComponent)
                .add(new BulletComponent(author, targetPosition, weaponModel.getDamage()));
        return player;
    }

    public void addBulletToEngine(Entity entity, Vector2 targetPosition, WeaponModel weaponModel) {
        engine.addEntity(bullet(entity, targetPosition, weaponModel));
        engine.getSystem(SoundsSystem.class).playShoot(targetPosition);
    }

    public Vector2 getPointNear(Vector2 basePosition, float precision) {
        double r = 10 / precision;

        double angle = random.nextInt(360);

        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x + x, basePosition.y + y);
    }

    public Entity spawnStalker(SpawnComponent spawnComponent, MobType mobType) {
        Entity entity = new Entity();

        WeaponModel w = new WeaponModel();
        w.setSpeed(14);
        w.setDamage(2);
        w.setPrecision(10);
        w.setBulletQuantity(30);

        MoodComponent moodComponent = new MoodComponent(mobType.group, spawnComponent.getRelations(), spawnComponent.getSpawnModel().isAngry());
        moodComponent.ignorePlayer = spawnComponent.getSpawnModel().isIgnorePlayer();

        entity.add(new PositionComponent(spawnComponent.getTargeting().getTarget()))
                .add(new VelocityComponent())
                .add(new HealthComponent())
                .add(new VisionComponent())
                .add(moodComponent)
                .add(new GraphMotionComponent(null))
                .add(new StalkerComponent(contentGenerator.generateName(), new ArrayList<>()))
                .add(new WeaponComponent(w, this))
                .add(new StatesComponent(entity, spawnComponent.getDispatcher()))
                .add(new TargetMovingComponent(spawnComponent.getTargeting()))
                .add(new RelationalSpriteComponent(8, 8));

        return entity;
    }

    public Entity randomStalker(Vector2 position, TargetMovingComponent.Targeting targeting) {
        Entity entity = new Entity();

        WeaponModel w = new WeaponModel();
        w.setSpeed(30);
        w.setDamage(1);
        w.setPrecision(10);
        w.setBulletQuantity(15);

        entity.add(new PositionComponent(position))
                .add(new VisionComponent())
                .add(new RelationalSpriteComponent(8, 8))
                .add(new VelocityComponent())
                .add(new HealthComponent())
                .add(new GraphMotionComponent(null))
                .add(new WeaponComponent(w, this))
                .add(new StalkerComponent("Мутант", new ArrayList<ItemModel>()))
                .add(new StatesComponent(entity, null))
                .add(new MoodComponent(-1, null, true))
                .add(new TargetMovingComponent(targeting));
        Gdx.app.log("WorldSystem", "New entity created.");
        return entity;

    }

}
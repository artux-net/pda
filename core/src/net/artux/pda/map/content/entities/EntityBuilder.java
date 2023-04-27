package net.artux.pda.map.content.entities;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.BulletComponent;
import net.artux.pda.map.engine.ecs.components.FogOfWarComponent;
import net.artux.pda.map.engine.ecs.components.GraphMotionComponent;
import net.artux.pda.map.engine.ecs.components.GroupComponent;
import net.artux.pda.map.engine.ecs.components.GroupTargetMovingComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.LeaderComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.StalkerComponent;
import net.artux.pda.map.engine.ecs.components.StatesComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.WeaponComponent;
import net.artux.pda.map.engine.ecs.components.effects.Effects;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.engine.ecs.components.states.StalkerState;
import net.artux.pda.map.engine.ecs.entities.Bodies;
import net.artux.pda.map.engine.ecs.systems.statemachine.MessagingCodes;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WeaponModel;

import javax.inject.Inject;

@PerGameMap
public class EntityBuilder {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private final AssetManager assetManager;
    private final ContentGenerator contentGenerator;
    private final Texture bulletTexture;
    private final World world;

    @Inject
    public EntityBuilder(AssetManager assetManager, ContentGenerator contentGenerator, World world) {
        this.assetManager = assetManager;
        this.contentGenerator = contentGenerator;
        this.world = world;

        bulletTexture = assetManager.get("textures/icons/entity/bullet.png", Texture.class);
    }

    public Entity player(Vector2 position, DataRepository dataRepository) {
        return new Entity()
                .add(new BodyComponent(Bodies.stalker(position, world)))
                .add(new VelocityComponent())
                .add(new VisionComponent())
                .add(new Effects())
                .add(new SpriteComponent(assetManager.get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(dataRepository, assetManager))
                .add(new MoodComponent(dataRepository.getStoryDataModel()))
                .add(new HealthComponent())
                .add(new PlayerComponent(dataRepository.getStoryDataModel()));
    }

    public Entity bullet(Entity author, Entity target, WeaponModel weaponModel) {
        BodyComponent bodyComponent = pm.get(author);

        Vector2 targetPosition = pm.get(target).getPosition();
        targetPosition = getPointNear(targetPosition, weaponModel.getPrecision());

        Vector2 velocityUnit = targetPosition.cpy().sub(bodyComponent.getPosition()).nor();

        float vX = velocityUnit.x * weaponModel.getSpeed() * 1000;
        float vY = velocityUnit.y * weaponModel.getSpeed() * 1000;

        Vector2 direction = targetPosition.cpy().sub(bodyComponent.getPosition());
        float degrees = (float) (Math.atan2(
                -direction.x,
                direction.y
        ) * 180.0d / Math.PI);

        SpriteComponent spriteComponent = new SpriteComponent(bulletTexture, 15, 2);
        spriteComponent.setRotation(degrees + 90);

        return new Entity()
                .add(new BodyComponent(bodyComponent.getPosition(), BodyDef.BodyType.KinematicBody, world).velocity((double) vX * 10000, (double) vY * 10000))
                .add(spriteComponent)
                .add(new FogOfWarComponent())
                .add(new BulletComponent(author, target, targetPosition, weaponModel.getDamage()));
    }

    public Vector2 getPointNear(Vector2 basePosition, float precision) {
        double r = 40 / precision;

        double angle = random.nextInt(360);

        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x + x, basePosition.y + y);
    }

    public Entity createStalerGroup(Vector2 position) {
        return null;
    }


    public Entity createGroupStalker(Vector2 position, GroupComponent group) {
        Entity entity = new Entity();

        WeaponModel w = new WeaponModel();
        w.setType(ItemType.RIFLE);
        switch (group.getStrength()) {
            case STRONG:
                w.setSpeed(random(13, 15));
                w.setDamage(random(9, 10));
                w.setPrecision(random(35, 45));
                w.setBulletQuantity(45);
                break;
            case MIDDLE:
                w.setSpeed(random(9, 15));
                w.setDamage(random(5, 10));
                w.setPrecision(random(25, 45));
                w.setBulletQuantity(45);
                break;
            default:
                w.setSpeed(random(5, 10));
                w.setDamage(random(3, 5));
                w.setPrecision(random(15, 25));
                w.setBulletQuantity(30);
                break;
        }

        HealthComponent healthComponent = new HealthComponent();
        healthComponent.setImmortal(group.getParams().contains("immortal"));

        StatesComponent statesComponent = new StatesComponent(entity, group.getDispatcher(), StalkerState.INITIAL, StalkerState.GUARDING);
        group.getDispatcher().addListener(statesComponent, MessagingCodes.ATTACKED);

        entity
                .add(new BodyComponent(Bodies.stalker(position, world)))
                .add(new GraphMotionComponent(null))
                .add(new VelocityComponent())
                .add(new VisionComponent())
                .add(new Effects())
                .add(healthComponent)
                .add(group)
                .add(new MoodComponent(group))
                .add(new StalkerComponent(contentGenerator.generateName(), contentGenerator.generateAvatar(), contentGenerator.getRandomItems()))
                .add(new WeaponComponent(w, assetManager))
                .add(statesComponent)
                .add(new GroupTargetMovingComponent(group))
                .add(new FogOfWarComponent());

        return entity;
    }

    public Entity randomMutant(GroupTargetMovingComponent.Targeting targeting) {
        Entity entity = new Entity();

        WeaponModel w = new WeaponModel();
        w.setSpeed(random(5, 10));
        w.setDamage(random(3, 5));
        w.setPrecision(random(15, 25));
        w.setBulletQuantity(30);

        StatesComponent statesComponent = new StatesComponent(entity, null, StalkerState.INITIAL, StalkerState.GUARDING);

    /*    entity.add(new BodyComponent(Bodies.stalker(targeting.getTarget(), world)))
                .add(new VisionComponent())
                .add(new SpriteComponent(assetManager.get("textures/icons/entity/mutant.png"), 8, 8))
                .add(new VelocityComponent())
                .add(new HealthComponent())
                .add(new GraphMotionComponent(null))
                .add(new WeaponComponent(w, assetManager))
                .add(new StalkerComponent("Мутант","textures/icons/entity/mutant.png", new ArrayList<ItemModel>()))
                .add(statesComponent)
                .add(new MoodComponent(-1, null, Collections.singleton("angry")))
                .add(new GroupTargetMovingComponent(targeting));*/

        Gdx.app.getApplicationLogger().log("WorldSystem", "New entity created.");
        return entity;
    }


    public Entity createLeader(Vector2 pos, GroupComponent groupComponent) {
        Entity entity = createGroupStalker(pos, groupComponent);
        entity.add(new LeaderComponent());
        return entity;
    }
}
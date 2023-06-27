package net.artux.pda.map.content.entities;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.ai.GraphMotionComponent;
import net.artux.pda.map.ecs.ai.LeaderComponent;
import net.artux.pda.map.ecs.ai.StalkerComponent;
import net.artux.pda.map.ecs.ai.StalkerGroup;
import net.artux.pda.map.ecs.ai.StatesComponent;
import net.artux.pda.map.ecs.ai.TargetMovingComponent;
import net.artux.pda.map.ecs.ai.statemachine.MessagingCodes;
import net.artux.pda.map.ecs.ai.states.MutantState;
import net.artux.pda.map.ecs.ai.states.StalkerState;
import net.artux.pda.map.ecs.battle.BulletComponent;
import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.ecs.battle.WeaponComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.effects.EffectsComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.render.SpriteComponent;
import net.artux.pda.map.ecs.vision.FogOfWarComponent;
import net.artux.pda.map.ecs.vision.VisionComponent;
import net.artux.pda.map.engine.entities.Bodies;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.WeaponModel;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Отвечает за построение сущностей на карте
 */
@PerGameMap
public class EntityBuilder {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ApplicationLogger logger;
    private final AssetManager assetManager;
    private final StrengthUpdater strengthUpdater;
    private final ContentGenerator contentGenerator;
    private final LocaleBundle localeBundle;
    private final Texture bulletTexture;
    private final World world;

    @Inject
    public EntityBuilder(ApplicationLogger logger, AssetManager assetManager,
                         StrengthUpdater strengthUpdater,
                         ContentGenerator contentGenerator, LocaleBundle localeBundle, World world) {
        this.logger = logger;
        this.assetManager = assetManager;
        this.strengthUpdater = strengthUpdater;
        this.contentGenerator = contentGenerator;
        this.localeBundle = localeBundle;
        this.world = world;

        bulletTexture = assetManager.get("textures/icons/entity/bullet.png", Texture.class);
    }

    public Entity player(Vector2 position, DataRepository dataRepository) {
        return new Entity()
                .add(new BodyComponent(Bodies.stalker(position, world)))
                .add(new VisionComponent())
                .add(new EffectsComponent())
                .add(new SpriteComponent(assetManager.get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(dataRepository, assetManager))
                .add(new MoodComponent(dataRepository.getInitDataModel()))
                .add(new HealthComponent(dataRepository))
                .add(new PlayerComponent());
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

    public Entity createGroupStalker(Vector2 position, StalkerGroup stalkerGroup) {
        Entity entity = new Entity();

        HealthComponent healthComponent = new HealthComponent(new ArmorModel());
        healthComponent.setImmortal(stalkerGroup.getParams().contains("immortal"));

        StatesComponent statesComponent = new StatesComponent(entity, stalkerGroup.getDispatcher(), StalkerState.INITIAL, StalkerState.GUARDING);
        stalkerGroup.getDispatcher().addListener(statesComponent, MessagingCodes.ATTACKED);

        entity
                .add(new BodyComponent(Bodies.stalker(position, world)))
                .add(new GraphMotionComponent(null))
                .add(new VisionComponent())
                .add(new EffectsComponent())
                .add(healthComponent)
                .add(stalkerGroup)
                .add(new MoodComponent(stalkerGroup))
                .add(new StalkerComponent(contentGenerator.generateStalkerName(), contentGenerator.generateStalkerAvatar(), contentGenerator.getRandomItems()))
                .add(statesComponent)
                .add(new TargetMovingComponent(stalkerGroup))
                .add(new FogOfWarComponent());
        strengthUpdater.updateStalker(entity, stalkerGroup.getStrength());
        return entity;
    }

    public Entity randomMutant(TargetMovingComponent.Targeting targeting) {
        Entity entity = new Entity();
        StatesComponent statesComponent = new StatesComponent(entity, new MessageDispatcher(), MutantState.INITIAL, MutantState.GUARDING);

        BodyComponent bodyComponent = new BodyComponent(Bodies.mutant(targeting.getTarget(), world));
        bodyComponent.setMovementForce(2000);

        MutantType randomType = MutantType.values()[random.nextInt(MutantType.values().length)];
        entity.add(bodyComponent)
                .add(new VisionComponent())
                .add(new FogOfWarComponent())
                .add(new HealthComponent(new ArmorModel()))
                .add(new GraphMotionComponent(null))
                .add(randomType.getInfightingComponent())
                .add(statesComponent)
                .add(new MoodComponent())
                .add(new TargetMovingComponent(targeting))
                .add(new StalkerComponent(localeBundle.get(randomType.getTitleId()), randomType.getAvatarId(), new ArrayList<>()))
                .add(new SpriteComponent(assetManager.get(randomType.getIconId()), 8, 8));

        logger.log("WorldSystem", "Mutant " + randomType + " created.");
        return entity;
    }

    public Entity createLeader(Vector2 pos, StalkerGroup stalkerGroup) {
        Entity entity = createGroupStalker(pos, stalkerGroup);
        entity.add(new LeaderComponent());
        return entity;
    }
}
package net.artux.pda.map.engine.ecs.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.Group;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.map.ConditionComponent;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.engine.ecs.entities.model.GangRelations;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.dialog.ControlPointDialog;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.map.Strength;
import net.artux.pda.model.user.Gang;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class EntityProcessorSystem extends EntitySystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private final EntityBuilder builder;
    private final AssetManager assetManager;
    private final RenderSystem renderSystem;
    private final GangRelations gangRelations;
    private final World world;
    private final MapOrientationSystem mapOrientationSystem;
    private final LocaleBundle localeBundle;

    private final ControlPointDialog controlPointDialog;
    private final UserInterface userInterface;

    @Inject
    public EntityProcessorSystem(EntityBuilder entityBuilder, AssetManager assetManager,
                                 RenderSystem renderSystem, World world,
                                 MapOrientationSystem mapOrientationSystem,
                                 LocaleBundle localeBundle, ControlPointDialog controlPointDialog1, UserInterface userInterface) {
        super();
        this.world = world;
        builder = entityBuilder;
        this.renderSystem = renderSystem;
        this.assetManager = assetManager;
        this.mapOrientationSystem = mapOrientationSystem;
        this.localeBundle = localeBundle;
        this.controlPointDialog = controlPointDialog1;
        this.userInterface = userInterface;
        JsonReader reader = new JsonReader(Gdx.files.internal("config/mobs.json").reader());
        gangRelations = new Gson().fromJson(reader, GangRelations.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(Family.all(BodyComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {

            }

            @Override
            public void entityRemoved(Entity entity) {
                world.destroyBody(pm.get(entity).body);
            }
        });
    }

    public void addEntity(Entity entity) {
        getEngine().addEntity(entity);
    }

    public Entity generateSpawn(SpawnModel spawnModel, boolean withSprite) {
        Gang gang = spawnModel.getGroup();
        SpawnComponent spawnComponent = new SpawnComponent(spawnModel);
        Vector2 position = spawnComponent.getPosition();
        Entity entity = new Entity();

        if (gang != null) {
            Group group = generateGroup(position, gang,
                    spawnModel.getN(), spawnModel.getParams(), spawnModel.getStrength());
            spawnComponent.setGroup(group);

            float size = spawnModel.getR() * 2 * 0.9f;
            if (withSprite)
                entity.add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size));
            if (withSprite)
                //add click component
                entity.add(new ClickComponent(spawnModel.getR(),
                        () -> {
                            controlPointDialog.update(group, spawnComponent);
                            controlPointDialog.show(userInterface.getStage());
                        }));

            if (spawnModel.getCondition() != null)
                entity.add(new ConditionComponent(spawnModel.getCondition()));
            else
                entity.add(new ConditionComponent(Collections.emptyMap()));

            entity.add(new BodyComponent(position, world))
                    .add(spawnComponent);
        }
        addEntity(entity);
        return entity;
    }

    public void addBulletToEngine(Entity entity, Entity target, WeaponModel weaponModel) {
        Entity bullet = builder.bullet(entity, target, weaponModel);
        getEngine().addEntity(bullet);
    }

    public Group generateGroup(Gang gang) {
        return generateGroup(gang, random(4, 7));
    }

    public Group generateGroup(Gang gang, int n) {
        Vector2 randomPosition = mapOrientationSystem.getPointToSpawnEntity();
        return generateGroup(randomPosition, gang, n, Collections.emptySet(), Strength.WEAK);
    }

    public Group generateGroup(Vector2 pos, Gang gang, int n) {
        return generateGroup(pos, gang, n, Collections.emptySet(), Strength.WEAK);
    }

    public Group generateGroup(Vector2 pos, Gang gang, int n, Strength strength) {
        return generateGroup(pos, gang, n, Collections.emptySet(), strength);
    }

    public Group generateGroup(Vector2 pos, Gang gang, int n, Set<String> params, Strength strength) {
        Integer[] relations = gangRelations.get(gang);
        Group group = new Group(gang, relations, strength, params);

        Entity leader = builder.createLeader(pos, group);
        group.addEntity(leader);
        leader.add(new ClickComponent(10, () ->
                renderSystem.showText(localeBundle.get(group.getGang().getTitleId()), pm.get(leader).getPosition())));
        getEngine().addEntity(leader);

        for (int i = 0; i < n - 1; i++) {
            Entity entity = builder.createGroupStalker(getRandomAround(pos, 15), group);
            getEngine().addEntity(entity);
            group.addEntity(entity);
        }
        return group;
    }

    public Group generateTakeSpawnGroup(Vector2 attackTarget) {
        Gang gang = gangRelations.random();
        return generateTakeSpawnGroup(gang, attackTarget);
    }

    public Group generateTakeSpawnGroup(Gang gang, Vector2 attackTarget) {
        Group group = generateGroup(gang, random(4, 7));
        group.setTargeting(() -> attackTarget);
        return group;
    }

    public void generateAttackSpawnGroup(SpawnComponent spawnComponent) {
        Gang currentGang = spawnComponent.getSpawnModel().getGroup();
        Gang enemyGang = gangRelations.findEnemyByGang(currentGang);
        if (enemyGang != null) {
            Group group = generateGroup(enemyGang, random(4, 7));
            group.setTargeting(spawnComponent::getPosition);
        }
    }

    public Vector2 getRandomAround(Vector2 point, int r) {
        float offsetX = random(-r, r);
        float offsetY = random(-r, r);
        return point.cpy().add(offsetX, offsetY);
    }

}
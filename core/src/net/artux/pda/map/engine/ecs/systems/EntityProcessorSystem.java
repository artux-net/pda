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
import com.badlogic.gdx.utils.Pool;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.GroupComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.map.ConditionComponent;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.engine.ecs.entities.model.GangRelations;
import net.artux.pda.map.utils.di.scope.PerGameMap;
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
    private final LocaleBundle localeBundle;

    @Inject
    public EntityProcessorSystem(EntityBuilder entityBuilder, AssetManager assetManager, RenderSystem renderSystem, World world, LocaleBundle localeBundle) {
        super();
        this.world = world;
        builder = entityBuilder;
        this.renderSystem = renderSystem;
        this.assetManager = assetManager;
        this.localeBundle = localeBundle;
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

    public Entity generateSpawn(SpawnModel spawnModel) {
        Gang gang = spawnModel.getGroup();
        SpawnComponent spawnComponent = new SpawnComponent(spawnModel);
        Vector2 position = spawnComponent.getPosition();
        Entity controlPoint = new Entity();

        if (gang != null) {
            GroupComponent group = generateGroup(position, gang, gangRelations.get(gang),
                    spawnModel.getN(), spawnModel.getParams(), spawnModel.getStrength());
            spawnComponent.setGroup(group);

            float size = spawnModel.getR() * 2 * 0.9f;
            if (!spawnModel.getParams().contains("hide"))
                controlPoint.add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size));
            if (spawnModel.getCondition() != null)
                controlPoint.add(new ConditionComponent(spawnModel.getCondition()));
            else
                controlPoint.add(new ConditionComponent(Collections.emptyMap()));

            controlPoint.add(new BodyComponent(position, world))
                    .add(spawnComponent);
        }
        addEntity(controlPoint);
        return controlPoint;
    }


    public GroupComponent generateGroup(Vector2 pos, Gang gang, Integer[] relations, int n, Set<String> params, Strength strength) {
        GroupComponent groupComponent = new GroupComponent(gang, relations, strength, params);

        Entity leader = builder.createLeader(pos, groupComponent);
        groupComponent.addEntity(leader);
        leader.add(new ClickComponent(10, () ->
                renderSystem.showText(localeBundle.get(groupComponent.getGang().getTitleId()), pm.get(leader).getPosition())));
        getEngine().addEntity(leader);

        for (int i = 0; i < n; i++) {
            Entity entity = builder.createGroupStalker(getRandomAround(pos, 15), groupComponent);
            getEngine().addEntity(entity);
            groupComponent.addEntity(entity);
        }
        return groupComponent;
    }

    public void addBulletToEngine(Entity entity, Entity target, WeaponModel weaponModel) {
        Entity bullet = builder.bullet(entity, target, weaponModel);
        getEngine().addEntity(bullet);
    }

    public void generateTakeSpawnGroup(Vector2 randomTransferPosition, Vector2 attackTarget) {
        Gang gang = gangRelations.random();
        GroupComponent group = generateGroup(randomTransferPosition, gang,
                gangRelations.get(gang), random(4, 7), Collections.emptySet(), Strength.WEAK);
        group.setTargeting(() -> attackTarget);
    }

    public void generateAttackSpawnGroup(Vector2 randomTransferPosition, SpawnComponent spawnComponent) {
        Gang currentGang = spawnComponent.getSpawnModel().getGroup();
        Gang enemyGang = gangRelations.findEnemyByGang(currentGang);
        if (enemyGang != null) {
            GroupComponent group = generateGroup(randomTransferPosition, enemyGang,
                    gangRelations.get(enemyGang), random(4, 7), Collections.emptySet(), Strength.WEAK);
            group.setTargeting(spawnComponent::getPosition);
        }
    }

    public Vector2 getRandomAround(Vector2 point, int r) {
        float offsetX = random(-r, r);
        float offsetY = random(-r, r);
        return point.cpy().add(offsetX, offsetY);
    }

}
package net.artux.pda.map.engine.ecs.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.repository.RandomPosition;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.GroupComponent;
import net.artux.pda.map.engine.ecs.components.Position;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.map.ConditionComponent;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.engine.helpers.EntityBuilder;
import net.artux.pda.map.model.GangRelations;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.user.Gang;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class EntityProcessorSystem extends EntitySystem {

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    private final EntityBuilder builder;
    private final AssetManager assetManager;
    private final RenderSystem renderSystem;
    private final GangRelations gangRelations;

    @Inject
    public EntityProcessorSystem(EntityBuilder entityBuilder, AssetManager assetManager, RenderSystem renderSystem) {
        super();
        builder = entityBuilder;
        this.renderSystem = renderSystem;
        this.assetManager = assetManager;
        JsonReader reader = new JsonReader(Gdx.files.internal("config/mobs.json").reader());
        gangRelations = new Gson().fromJson(reader, GangRelations.class);
    }

    public void addEntity(Entity entity) {
        getEngine().addEntity(entity);
    }

    public Entity generateSpawn(SpawnModel spawnModel) {
        Gang gang = spawnModel.getGroup();
        if (gang != null) {
            SpawnComponent spawnComponent = new SpawnComponent(spawnModel);
            spawnComponent.setGroup(generateGroup(spawnComponent.getPosition(), gang, gangRelations.get(gang), spawnModel.getN(), spawnModel.getParams()));

            Entity controlPoint = new Entity();
            float size = spawnModel.getR() * 2 * 0.9f;
            if (!spawnModel.getParams().contains("hide")) {
                controlPoint.add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size));
            }
            if (spawnModel.getCondition() != null)
                controlPoint.add(new ConditionComponent(spawnModel.getCondition()));
            else
                controlPoint.add(new ConditionComponent(Collections.emptyMap()));

            controlPoint.add(new Position(Mappers.vector2(spawnModel.getPos())))
                    .add(spawnComponent)
                    .add(new ClickComponent(spawnModel.getR(),
                            () -> renderSystem.showText(spawnComponent.desc(), Mappers.vector2(spawnModel.getPos()))));

            if (spawnModel.getCondition() != null)
                controlPoint.add(new ConditionComponent(spawnModel.getCondition()));
            else
                controlPoint.add(new ConditionComponent(Collections.emptyMap()));

            addEntity(controlPoint);
        }
        return null;
    }


    public GroupComponent generateGroup(Vector2 pos, Gang gang, Integer[] relations, int n, Set<String> params) {
        List<Entity> pointEntities = new LinkedList<>();
        GroupComponent groupComponent = new GroupComponent(gang, relations, pointEntities, params);
        for (int i = 0; i < n; i++) {
            Entity entity = builder.spawnStalker(RandomPosition.getRandomAround(pos, 15), groupComponent);
            getEngine().addEntity(entity);
            pointEntities.add(entity);
        }
        return groupComponent;
    }

    public void addBulletToEngine(Entity entity, Entity target, WeaponModel weaponModel) {
        Entity bullet = builder.bullet(entity, target, weaponModel);
        getEngine().addEntity(bullet);

    }

    public void generateTakeSpawnGroup(Vector2 randomTransferPosition, Vector2 attackTarget) {
        Gang gang = gangRelations.random();
        GroupComponent group = generateGroup(randomTransferPosition, gang, gangRelations.get(gang), random(4, 7), Collections.emptySet());
        group.setTargeting(() -> attackTarget);
    }

    public void generateAttackSpawnGroup(Vector2 randomTransferPosition, SpawnComponent spawnComponent) {
        Gang currentGang = spawnComponent.getSpawnModel().getGroup();
        Gang enemyGang = gangRelations.findEnemyByGang(currentGang);
        if (enemyGang != null) {
            GroupComponent group = generateGroup(randomTransferPosition, enemyGang, gangRelations.get(enemyGang), random(4, 7), Collections.emptySet());
            group.setTargeting(spawnComponent::getPosition);
        }
    }


}
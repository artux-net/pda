package net.artux.pda.map.ecs.creation;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.ai.MapOrientationSystem;
import net.artux.pda.map.ecs.ai.EntityComponent;
import net.artux.pda.map.ecs.ai.StalkerGroup;
import net.artux.pda.map.ecs.global.WorldSystem;
import net.artux.pda.map.ecs.interactive.ClickComponent;
import net.artux.pda.map.ecs.interactive.map.ConditionComponent;
import net.artux.pda.map.ecs.interactive.map.SpawnComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.render.SpriteComponent;
import net.artux.pda.map.engine.entities.model.GangRelations;
import net.artux.pda.map.repository.SavedSpawn;
import net.artux.pda.map.repository.SavedStalker;
import net.artux.pda.map.view.root.UserInterface;
import net.artux.pda.map.view.dialog.ControlPointDialog;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.map.Strength;
import net.artux.pda.model.user.Gang;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class EntityProcessorSystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private final EntityBuilder builder;
    private final AssetManager assetManager;
    private final RenderSystem renderSystem;
    private final GangRelations gangRelations;
    private final World world;
    private final MapOrientationSystem mapOrientationSystem;
    private final LocaleBundle localeBundle;
    private final ContentGenerator contentGenerator;

    private final ControlPointDialog controlPointDialog;
    private final UserInterface userInterface;
    private final WorldSystem worldSystem;
    private final GameMap gameMap;
    private final ApplicationLogger logger;

    @Inject
    public EntityProcessorSystem(EntityBuilder entityBuilder, AssetManager assetManager,
                                 RenderSystem renderSystem, World world,
                                 MapOrientationSystem mapOrientationSystem,
                                 ContentGenerator contentGenerator,
                                 ApplicationLogger logger,
                                 GameMap gameMap,
                                 Gson gson,
                                 LocaleBundle localeBundle, ControlPointDialog controlPointDialog1, UserInterface userInterface, WorldSystem worldSystem) {
        super();
        this.logger = logger;
        this.world = world;
        this.gameMap = gameMap;
        builder = entityBuilder;
        this.renderSystem = renderSystem;
        this.assetManager = assetManager;
        this.mapOrientationSystem = mapOrientationSystem;
        this.localeBundle = localeBundle;
        this.controlPointDialog = controlPointDialog1;
        this.userInterface = userInterface;
        this.worldSystem = worldSystem;
        JsonReader reader = new JsonReader(Gdx.files.internal("config/mobs.json").reader());
        gangRelations = gson.fromJson(reader, GangRelations.class);
        this.contentGenerator = contentGenerator;
    }

    public void addEntity(Entity entity) {
        worldSystem.addEntity(entity);
    }

    public Entity generateNewSpawn(SpawnModel spawnModel, boolean withSprite) {
        SpawnComponent spawnComponent = new SpawnComponent(spawnModel);
        Vector2 position = spawnComponent.getPosition();
        Entity spawnEntity = new Entity();

        float size = spawnModel.getR() * 2 * 0.9f;
        if (withSprite) {
            spawnEntity.add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size));
            spawnEntity.add(new ClickComponent(spawnModel.getR(), () -> {
                controlPointDialog.update(spawnComponent);
                controlPointDialog.show(userInterface.getStage());
            }));
        }

        spawnEntity.add(new ConditionComponent(spawnModel.getCondition()))
                .add(new BodyComponent(position, world))
                .add(spawnComponent);
        addEntity(spawnEntity);
        return spawnEntity;
    }

    public void startBullet(Entity entity, Entity target, WeaponModel weaponModel) {
        builder.bullet(entity, target, weaponModel);
    }

    public void startBullet(Entity entity, WeaponModel weaponModel) {
        builder.bullet(entity, weaponModel);
    }

    public StalkerGroup restoreGroup(Vector2 position, SavedSpawn savedSpawn) {
        Gang gang = savedSpawn.getGang();
        int count = savedSpawn.getStalkers().size();
        Iterator<SavedStalker> stalkerIterator = savedSpawn.getStalkers().iterator();
        StalkerGroup stalkerGroup = generateNewGroup(position, gang, count, savedSpawn.getStrength());
        Array<Entity> stalkers = stalkerGroup.getEntities();

        for (int i = 0; i < stalkers.size; i++) {
            Entity entity = stalkers.get(i);
            SavedStalker savedStalker = stalkerIterator.next();
            entity.add(new EntityComponent(savedStalker.getName(), savedStalker.getAvatar(), contentGenerator.getRandomItems()));
        }

        return stalkerGroup;
    }

    public StalkerGroup generateNewGroup(Gang gang) {
        return generateNewGroup(gang, random(4, 7));
    }

    public StalkerGroup generateNewGroup(Gang gang, int n) {
        Vector2 randomPosition = mapOrientationSystem.getPointToSpawnEntity();
        return generateNewGroup(randomPosition, gang, n, Collections.emptySet(), Strength.WEAK);
    }

    public StalkerGroup generateNewGroup(Vector2 pos, Gang gang, int n) {
        return generateNewGroup(pos, gang, n, Collections.emptySet(), Strength.WEAK);
    }

    public StalkerGroup generateNewGroup(Vector2 pos, SpawnModel spawnModel) {
        Gang gang = spawnModel.getGroup();
        return generateNewGroup(pos, gang, spawnModel.getN(), spawnModel.getParams(), spawnModel.getStrength());
    }

    public StalkerGroup generateNewGroup(Vector2 pos, Gang gang, int n, Strength strength) {
        return generateNewGroup(pos, gang, n, Collections.emptySet(), strength);
    }

    public StalkerGroup generateNewGroup(Vector2 pos, Gang gang, int n, Set<String> params, Strength strength) {
        Integer[] relations = gangRelations.get(gang);
        StalkerGroup stalkerGroup = new StalkerGroup(gang, relations, strength, params);
        if (n > 0) {
            if (n > 1) {
                Entity leader = builder.createLeader(pos, stalkerGroup);
                stalkerGroup.addEntity(leader);
                leader.add(new ClickComponent(10, () ->
                        renderSystem.showText(localeBundle.get(stalkerGroup.getGang().getTitleId()), pm.get(leader).getPosition())));
                addEntity(leader);
                n--;
            }
            for (int i = 0; i < n; i++) {
                Entity entity = builder.createGroupStalker(getRandomAround(pos, 15), stalkerGroup);
                addEntity(entity);
                stalkerGroup.addEntity(entity);
            }
        }
        return stalkerGroup;
    }

    public StalkerGroup generateTakeSpawnGroup(Vector2 attackTarget) {
        Gang gang = gangRelations.random();
        return generateTakeSpawnGroup(gang, attackTarget);
    }

    public StalkerGroup generateTakeSpawnGroup(Gang gang, Vector2 attackTarget) {
        StalkerGroup stalkerGroup = generateNewGroup(gang, random(4, 7));
        stalkerGroup.setTargeting(() -> attackTarget);
        return stalkerGroup;
    }

    public void generateAttackSpawnGroup(SpawnComponent spawnComponent) {
        Gang currentGang = spawnComponent.getSpawnModel().getGroup();
        Gang enemyGang = gangRelations.findEnemyByGangFromCurrentMap(currentGang, gameMap);
        if (enemyGang != null) {
            StalkerGroup stalkerGroup = generateNewGroup(enemyGang, random(4, 7));
            stalkerGroup.setTargeting(spawnComponent::getPosition);
        }
    }

    public Vector2 getRandomAround(Vector2 point, int r) {
        float offsetX = random(-r, r);
        float offsetY = random(-r, r);
        return point.cpy().add(offsetX, offsetY);
    }

}
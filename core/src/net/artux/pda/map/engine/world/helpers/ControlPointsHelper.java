package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.ControlPointComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpawnComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.model.MobType;
import net.artux.pda.map.model.MobsTypes;
import net.artux.pda.map.model.SpawnModel;
import net.artux.pda.map.model.input.GameMap;

import java.util.LinkedList;
import java.util.List;

public class ControlPointsHelper {

    public static void createControlPointsEntities(final Engine engine, EntityBuilder entityBuilder, AssetManager assetManager) {
        JsonReader reader = new JsonReader(Gdx.files.internal("mobs.json").reader());
        MobsTypes mobsTypes = new Gson().fromJson(reader, MobsTypes.class);

        GameMap map = engine.getSystem(DataSystem.class).getMap();

        for (final SpawnModel spawnModel : map.getSpawns()) {
            MobType mobType = mobsTypes.getMobType(spawnModel.getId());

            SpawnComponent spawnComponent = new SpawnComponent(spawnModel, mobsTypes.getRelations(mobType.group).toArray(new Integer[0]));

            Entity controlPoint = new Entity();
            float size = spawnModel.getR() * 2 * 0.9f;

            List<Entity> pointEntities = new LinkedList<>();
            for (int i = 0; i < spawnModel.getN(); i++) {
                Entity entity = entityBuilder.spawnStalker(spawnComponent, mobType);
                engine.addEntity(entity);
                pointEntities.add(entity);
            }

            final ControlPointComponent controlPointComponent = new ControlPointComponent("Контрольная точка", mobType, pointEntities);

            controlPoint.add(new PositionComponent(spawnModel.getPosition()))
                    .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size))
                    .add(spawnComponent)
                    .add(controlPointComponent)
                    .add(new ClickComponent(() -> engine.getSystem(RenderSystem.class)
                            .showText(controlPointComponent.desc(), spawnModel.getPosition())));
            engine.addEntity(controlPoint);

        }
    }

}
package net.artux.pda.map.engine.world.helpers;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.ControlPointComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.model.MobType;
import net.artux.pda.map.model.MobsTypes;
import net.artux.pda.map.model.Spawn;
import net.artux.pda.map.model.input.GameMap;
import net.artux.pda.model.user.UserModel;

import java.util.LinkedList;
import java.util.List;

public class ControlPointsHelper {

    public static void createControlPointsEntities(final Engine engine, EntityBuilder entityBuilder, AssetManager assetManager) {
        JsonReader reader = new JsonReader(Gdx.files.internal("mobs.json").reader());
        MobsTypes mobsTypes = new Gson().fromJson(reader, MobsTypes.class);

        GameMap map = engine.getSystem(DataSystem.class).getMap();
        UserModel userModel = engine.getSystem(DataSystem.class).getMember();

        for (final Spawn spawn : map.getSpawns()) {
            MobType mobType = mobsTypes.getMobType(spawn.getId());

            Entity controlPoint = new Entity();
            float size = spawn.getR() * 2 * 0.9f;

            TargetMovingComponent.Targeting targeting = () -> {
                double r = (double) spawn.getR() / 2 + random.nextInt(spawn.getR());

                double angle = random.nextInt(360);

                Vector2 basePosition = spawn.getPosition();
                float x = (float) (Math.cos(angle) * r);
                float y = (float) (Math.sin(angle) * r);
                return new Vector2(basePosition.x + x, basePosition.y + y);
            };

            List<Entity> pointEntities = new LinkedList<>();
            for (int i = 0; i < spawn.getN(); i++) {
                Entity entity = entityBuilder.spawnStalker(spawn, mobType,mobsTypes, userModel.getRelations(), targeting);
                engine.addEntity(entity);
                pointEntities.add(entity);
            }

            final ControlPointComponent controlPointComponent = new ControlPointComponent("Контрольная точка", mobType, pointEntities);

            controlPoint.add(new PositionComponent(spawn.getPosition()))
                    .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size))
                    .add(controlPointComponent)
                    .add(new ClickComponent(() -> engine.getSystem(RenderSystem.class)
                            .showText(controlPointComponent.desc(), spawn.getPosition())));
            engine.addEntity(controlPoint);

        }
    }

}
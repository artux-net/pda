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

import net.artux.pda.map.engine.ContentGenerator;
import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.ControlPointComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.states.BotStatesAshley;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.MobType;
import net.artux.pda.map.model.MobsTypes;
import net.artux.pda.map.model.Spawn;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Item;
import net.artux.pdalib.profile.items.Weapon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ControlPointsHelper {

    public static void createControlPointsEntities(final Engine engine, AssetManager assetManager) {
        ContentGenerator contentGenerator = new ContentGenerator();
        JsonReader reader = new JsonReader(Gdx.files.internal("mobs.json").reader());
        MobsTypes mobsTypes = new Gson().fromJson(reader, MobsTypes.class);

        Map map = engine.getSystem(DataSystem.class).getMap();
        Member member = engine.getSystem(DataSystem.class).getMember();

        for (final Spawn spawn : map.getSpawns()) {
            MobType mobType = mobsTypes.getMobType(spawn.getId());

            Entity controlPoint = new Entity();
            float size = spawn.getR() * 2 * 0.9f;

            TargetMovingComponent.Targeting targeting = new TargetMovingComponent.Targeting() {
                @Override
                public Vector2 getTarget() {
                    double r = (double) spawn.getR() / 2 + random.nextInt(spawn.getR());

                    double angle = random.nextInt(360);

                    Vector2 basePosition = spawn.getPosition();
                    float x = (float) (Math.cos(angle) * r);
                    float y = (float) (Math.sin(angle) * r);
                    return new Vector2(basePosition.x + x, basePosition.y + y);
                }
            };

            List<Entity> pointEntities = new LinkedList<>();
            for (int i = 0; i < spawn.getN(); i++) {
                Entity entity = new Entity();

                Armor armor = new Armor();
                Weapon w = new Weapon();
                w.speed = 14;
                w.damage = 2;
                w.precision = 1;
                w.bullet_quantity = 30;

                MoodComponent moodComponent = new MoodComponent(mobType.group, mobsTypes.getRelations(mobType.group).toArray(new Integer[0]), spawn.isAngry());
                moodComponent.ignorePlayer = spawn.isIgnorePlayer();

                entity.add(new PositionComponent(targeting.getTarget()))
                        .add(new VelocityComponent())
                        .add(new HealthComponent())
                        .add(moodComponent)
                        .add(new StalkerComponent(contentGenerator.generateName(), new ArrayList<Item>()))
                        .add(new WeaponComponent(armor, w, w))
                        .add(new StatesComponent<>(entity, BotStatesAshley.STANDING, BotStatesAshley.GUARDING))
                        .add(new TargetMovingComponent(targeting));


                Texture texture;
                if (member != null) {
                    if (mobType.group < 0 || member.relations.get(mobType.group) < -2)
                        texture = assetManager.get("red.png", Texture.class);
                    else if (member.relations.get(mobType.group) > 2)
                        texture = assetManager.get("green.png", Texture.class);
                    else
                        texture = assetManager.get("yellow.png", Texture.class);
                } else texture = assetManager.get("yellow.png", Texture.class);

                entity.add(new SpriteComponent(texture, 8, 8));
                engine.addEntity(entity);
                pointEntities.add(entity);
            }

            final ControlPointComponent controlPointComponent = new ControlPointComponent("Контрольная точка", mobType, pointEntities);

            controlPoint.add(new PositionComponent(spawn.getPosition()))
                    .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size))
                    .add(controlPointComponent)
                    .add(new ClickComponent(new ClickComponent.ClickListener() {
                        @Override
                        public void clicked() {
                            engine.getSystem(RenderSystem.class)
                                    .showText(controlPointComponent.desc(),spawn.getPosition());
                        }
                    }));
            engine.addEntity(controlPoint);

        }
    }

}

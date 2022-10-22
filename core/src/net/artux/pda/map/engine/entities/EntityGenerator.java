package net.artux.pda.map.engine.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.ContentGenerator;
import net.artux.pda.map.engine.ai.states.BotStatesAshley;
import net.artux.pda.map.engine.components.BulletComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.engine.components.StateMachineComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.model.MobType;
import net.artux.pda.map.model.MobsTypes;
import net.artux.pda.map.model.Spawn;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.GangRelation;
import net.artux.pda.model.user.UserModel;

import java.util.ArrayList;

public class EntityGenerator {

    private final AssetManager assetManager;
    private final ContentGenerator contentGenerator;
    private final Texture bulletTexture;

    public EntityGenerator(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.contentGenerator = new ContentGenerator();
        bulletTexture = assetManager.get("bullet.png", Texture.class);
    }

    public Entity player(Vector2 position, StoryDataModel gdxData, UserModel userModel) {
        Entity player = new Entity();

        player.add(new PositionComponent(position))
                .add(new VelocityComponent())
                .add(new SpriteComponent(assetManager.get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(gdxData))
                .add(new MoodComponent(userModel))
                .add(new HealthComponent())
                .add(new UserVelocityInput())
                .add(new PlayerComponent(userModel, gdxData));
        return player;
    }

    public Entity bullet(Vector2 position, Vector2 targetPosition, WeaponModel weaponModel) {
        Entity player = new Entity();

        float targetX = targetPosition.x;
        float targetY = targetPosition.y;
        float vX = (float) ((targetX - position.x) /
                Math.sqrt(((targetX - position.x) * (targetX - position.x)) + ((targetY - position.y) * (targetY - position.y))));
        float vY = (float) ((targetY - position.y) /
                Math.sqrt(((targetY - position.y) * (targetY - position.y)) + ((targetX - position.x) * (targetX - position.x))));

        vX *= weaponModel.getSpeed();
        vY *= weaponModel.getSpeed();

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
                .add(new BulletComponent(targetPosition, weaponModel.getDamage()));
        return player;
    }

    public Entity spawnStalker(Spawn spawn, MobType mobType, MobsTypes mobsTypes, GangRelation relations, TargetMovingComponent.Targeting targeting) {
        Entity entity = new Entity();

        ArmorModel armor = new ArmorModel();
        WeaponModel w = new WeaponModel();
        w.setSpeed(14);
        w.setDamage(2);
        w.setPrecision(1);
        w.setBulletQuantity(30);

        MoodComponent moodComponent = new MoodComponent(mobType.group, mobsTypes.getRelations(mobType.group).toArray(new Integer[0]), spawn.isAngry());
        moodComponent.ignorePlayer = spawn.isIgnorePlayer();

        entity.add(new PositionComponent(targeting.getTarget()))
                .add(new VelocityComponent())
                .add(new HealthComponent())
                .add(moodComponent)
                .add(new StalkerComponent(contentGenerator.generateName(), new ArrayList<>()))
                .add(new WeaponComponent(w))
                .add(new StateMachineComponent<>(entity, BotStatesAshley.STANDING, BotStatesAshley.GUARDING))
                .add(new TargetMovingComponent(targeting));


        Texture texture;
        if (mobType.group < 0 || relations.getFor(Gang.ofId(mobType.group)) < -2)
            texture = assetManager.get("red.png", Texture.class);
        else if (relations.getFor(Gang.ofId(mobType.group)) > 2) //todo
            texture = assetManager.get("green.png", Texture.class);
        else
            texture = assetManager.get("yellow.png", Texture.class);

        entity.add(new SpriteComponent(texture, 8, 8));
        return entity;
    }

}

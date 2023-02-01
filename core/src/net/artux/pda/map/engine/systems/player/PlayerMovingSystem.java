package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.pathfinding.TiledNode;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.model.quest.story.StoryDataModel;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.Random;

import javax.inject.Inject;

@PerGameMap
public class PlayerMovingSystem extends BaseSystem {

    private final float MOVEMENT = 20f;
    private final float RUN_MOVEMENT = 30f;
    private final float PLAYER_MULTIPLICATION = 6f;

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private MapOrientationSystem mapOrientationSystem;

    public static boolean playerWalls = true;
    public static boolean speedup = false;
    public static boolean alwaysRun = false;

    private float weightCoefficient = 0f;

    private final HashMap<Integer, ImmutablePair<Sound, Sound>> stepSounds;
    private boolean left = false;
    private final float stepVolume = 0.15f;
    private final float oneSoundDistance = 5.7f;
    private float stepsDistance = 0;
    private final Random random;

    @Inject
    public PlayerMovingSystem(AssetManager assetManager, MapOrientationSystem mapOrientationSystem, DataRepository dataRepository) {
        super(Family.all(VelocityComponent.class, Position.class).exclude(PassivityComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
        dataRepository.addPropertyChangeListener(propertyChangeEvent -> {
            StoryDataModel storyDataModel = (StoryDataModel) propertyChangeEvent.getNewValue();
            weightCoefficient = 1.5f - storyDataModel.getTotalWeight() / 60;
        });

        weightCoefficient = 1.5f - dataRepository.getStoryDataModel().getTotalWeight() / 60;
        if (weightCoefficient < 0.1f)
            weightCoefficient = 0.1f;
        stepSounds = new HashMap<>();
        random = new Random();

        String prefix = "audio/sounds/steps/";

        stepSounds.put(TiledNode.TILE_EMPTY,
                ImmutablePair.of(assetManager.get(prefix + "empty1.ogg"), assetManager.get(prefix + "empty2.ogg")));

        stepSounds.put(TiledNode.TILE_ROAD,
                ImmutablePair.of(assetManager.get(prefix + "road1.ogg"), assetManager.get(prefix + "road2.ogg")));

        stepSounds.put(TiledNode.TILE_GRASS,
                ImmutablePair.of(assetManager.get(prefix + "grass1.ogg"), assetManager.get(prefix + "grass2.ogg")));

        stepSounds.put(TiledNode.TILE_SWAMP,
                ImmutablePair.of(assetManager.get(prefix + "swamp1.ogg"), assetManager.get(prefix + "swamp2.ogg")));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Entity entity = getPlayer();
        Position position = pm.get(entity);
        VelocityComponent velocityComponent = vm.get(entity);

        velocityComponent.setVelocity(velocityComponent.cpy());
        if (alwaysRun)
            velocityComponent.setRunning(true);

        Vector2 stepVector;
        Vector2 currentVelocity = velocityComponent.cpy().scl(weightCoefficient);
        if (speedup)
            currentVelocity.scl(PLAYER_MULTIPLICATION);

        HealthComponent healthComponent = hm.get(entity);

        float staminaDifference = 0;
        if (velocityComponent.isRunning() && healthComponent.stamina > 0) {
            stepVector = currentVelocity.scl(deltaTime).scl(RUN_MOVEMENT);
            if (!alwaysRun && entity == getPlayer())
                staminaDifference = -0.1f;
        } else {
            if (healthComponent.stamina < 100)
                staminaDifference = 0.06f;
            stepVector = currentVelocity.scl(deltaTime).scl(MOVEMENT);
        }

        healthComponent.stamina += staminaDifference;

        if (!stepVector.isZero()) {
            float newX = position.getX() + stepVector.x;
            float newY = position.getY() + stepVector.y;
            if (playerWalls) {
                if (insideMap(newX, position.getY()))
                    position.getPosition().x += stepVector.x * mapOrientationSystem.getMapBorder().getK(newX, position.getY());
                if (insideMap(position.getX(), newY))
                    position.getPosition().y += stepVector.y * mapOrientationSystem.getMapBorder().getK(position.getX(), newY);
            } else {
                if (insideMap(newX, position.getY()))
                    position.getPosition().x = newX;
                if (insideMap(position.getX(), newY))
                    position.getPosition().y = newY;
            }

            stepsDistance += stepVector.len();
            if (stepsDistance >= oneSoundDistance) {
                stepsDistance = 0;
                int type = mapOrientationSystem.getMapBorder().getTileType(position.x, position.y);
                if (!stepSounds.containsKey(type) || random.nextInt(4) == 0)
                    type = TiledNode.TILE_EMPTY;

                if (left)
                    stepSounds.get(type).left.play(stepVolume * currentVelocity.len());
                else
                    stepSounds.get(type).right.play(stepVolume * currentVelocity.len());
                left = !left;
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public boolean insideMap(float x, float y) {
        return (x <= GlobalData.mapWidth && x >= 0) && (y <= GlobalData.mapHeight && y >= 0);
    }

}

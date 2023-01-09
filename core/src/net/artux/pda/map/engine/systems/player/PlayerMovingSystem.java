package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.pathfinding.TiledNode;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;

import javax.inject.Inject;

@PerGameMap
public class PlayerMovingSystem extends BaseSystem {

    private final float MOVEMENT = 20f;
    private final float RUN_MOVEMENT = 30f;
    private final float PLAYER_MULTIPLICATION = 6f;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private MapOrientationSystem mapOrientationSystem;

    public static boolean playerWalls = false;
    public static boolean speedup = false;
    public static boolean alwaysRun = false;

    private final HashMap<Integer, ImmutablePair<Sound, Sound>> stepSounds;
    private boolean left = false;
    private final float stepVolume = 0.15f;
    private final float oneSoundDistance = 5.7f;
    private float stepsDistance = 0;

    @Inject
    public PlayerMovingSystem(AssetManager assetManager, MapOrientationSystem mapOrientationSystem) {
        super(Family.all(VelocityComponent.class, PositionComponent.class).exclude(PassivityComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
        stepSounds = new HashMap<>();

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
        PositionComponent positionComponent = pm.get(entity);
        VelocityComponent velocityComponent = vm.get(entity);

        velocityComponent.setVelocity(velocityComponent.cpy());
        if (alwaysRun)
            velocityComponent.setRunning(true);

        Vector2 stepVector;
        Vector2 currentVelocity = velocityComponent.cpy();
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
            float newX = positionComponent.getX() + stepVector.x;
            float newY = positionComponent.getY() + stepVector.y;
            if (playerWalls) {
                if (insideMap(newX, positionComponent.getY()))
                    positionComponent.getPosition().x += stepVector.x * mapOrientationSystem.getMapBorder().getK(newX, positionComponent.getY());
                if (insideMap(positionComponent.getX(), newY))
                    positionComponent.getPosition().y += stepVector.y * mapOrientationSystem.getMapBorder().getK(positionComponent.getX(), newY);
            } else {
                if (insideMap(newX, positionComponent.getY()))
                    positionComponent.getPosition().x = newX;
                if (insideMap(positionComponent.getX(), newY))
                    positionComponent.getPosition().y = newY;
            }

            stepsDistance += stepVector.len();
            if (stepsDistance >= oneSoundDistance) {
                stepsDistance = 0;
                int type = mapOrientationSystem.getMapBorder().getTileType(positionComponent.x, positionComponent.y);
                if (!stepSounds.containsKey(type))
                    type = TiledNode.TILE_EMPTY;
                
                if (left)
                    stepSounds.get(type).left.play(stepVolume);
                else
                    stepSounds.get(type).right.play(stepVolume);
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

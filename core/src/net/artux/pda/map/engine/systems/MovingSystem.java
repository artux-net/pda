package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.engine.data.GlobalData;

public class MovingSystem extends BaseSystem {

    private final float MOVEMENT = 10f;
    private final float RUN_MOVEMENT = 20f;
    private final float PLAYER_MULTIPLICATION = 6f;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<UserVelocityInput> uvm = ComponentMapper.getFor(UserVelocityInput.class);

    private MapOrientationSystem mapOrientationSystem;

    public static boolean playerWalls = true;
    public static boolean speedup = true;
    public static boolean alwaysRun = true;

    public MovingSystem() {
        super(Family.all(VelocityComponent.class, PositionComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        {
            Entity entity = player;

            VelocityComponent velocityComponent = vm.get(entity);
            UserVelocityInput userVelocityInput = uvm.get(entity);

            velocityComponent.setVelocity(userVelocityInput.getVelocity());
            if (speedup)
                velocityComponent.scl(PLAYER_MULTIPLICATION);
            if (alwaysRun)
                velocityComponent.setRunning(true);
        }

        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            PositionComponent positionComponent = pm.get(entity);
            VelocityComponent velocityComponent = vm.get(entity);
            Vector2 stepVector;
            Vector2 currentVelocity = velocityComponent.cpy();

            if (hm.has(entity)) {
                HealthComponent healthComponent = hm.get(entity);

                float staminaDifference = 0;
                if (velocityComponent.isRunning() && healthComponent.stamina > 0) {
                    stepVector = currentVelocity.scl(deltaTime).scl(RUN_MOVEMENT);
                    if (!alwaysRun && entity == player)
                        staminaDifference = -0.1f;
                } else {
                    if (healthComponent.stamina < 100)
                        staminaDifference = 0.06f;
                    stepVector = currentVelocity.scl(deltaTime).scl(MOVEMENT);
                }

                healthComponent.stamina += staminaDifference;
            } else {
                stepVector = currentVelocity.scl(deltaTime).scl(RUN_MOVEMENT);
            }


            if (!stepVector.isZero()) {
                float newX = positionComponent.getX() + stepVector.x;
                float newY = positionComponent.getY() + stepVector.y;
                if (uvm.has(entity) && playerWalls) {
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
            }
        }
    }

    public boolean insideMap(float x, float y) {
        return (x <= GlobalData.mapWidth && x >= 0) && (y <= GlobalData.mapHeight && y >= 0);
    }

    public int sign(float v) {
        return v < 0 ? -1 : 1;
    }

}

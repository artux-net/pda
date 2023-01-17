package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.data.GlobalData;

import javax.inject.Inject;

@PerGameMap
public class MovingSystem extends BaseSystem {

    private final float MOVEMENT = 20f;
    private final float RUN_MOVEMENT = 30f;

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private MapOrientationSystem mapOrientationSystem;

    @Inject
    public MovingSystem(AssetManager assetManager, MapOrientationSystem mapOrientationSystem) {
        super(Family.all(VelocityComponent.class, Position.class).exclude(PassivityComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity == getPlayer())
            return;

        Position position = pm.get(entity);
        VelocityComponent velocityComponent = vm.get(entity);
        Vector2 stepVector;
        Vector2 currentVelocity = velocityComponent.cpy();

        if (hm.has(entity)) {
            HealthComponent healthComponent = hm.get(entity);

            float staminaDifference = 0;
            if (velocityComponent.isRunning() && healthComponent.stamina > 0) {
                stepVector = currentVelocity.scl(deltaTime).scl(RUN_MOVEMENT);
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
            float newX = position.getX() + stepVector.x;
            float newY = position.getY() + stepVector.y;
            {
                if (insideMap(newX, position.getY()))
                    position.getPosition().x = newX;
                if (insideMap(position.getX(), newY))
                    position.getPosition().y = newY;
            }
        }
        if (!velocityComponent.isConstant() && entity != getPlayer())
            velocityComponent.set(0, 0);
    }

    public boolean insideMap(float x, float y) {
        return (x <= GlobalData.mapWidth && x >= 0) && (y <= GlobalData.mapHeight && y >= 0);
    }


}

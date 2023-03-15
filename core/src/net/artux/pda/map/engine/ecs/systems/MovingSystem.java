package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;

import javax.inject.Inject;

public class MovingSystem extends BaseSystem {

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private World world;

    @Inject
    public MovingSystem(World world) {
        super(Family.all(BodyComponent.class).exclude(PassivityComponent.class).get());
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(Family.all(BodyComponent.class).exclude(PassivityComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {

            }

            @Override
            public void entityRemoved(Entity entity) {
                world.destroyBody(pm.get(entity).body);
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
/*
        BodyComponent bodyComponent = pm.get(entity);
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
            float newX = bodyComponent.getX() + stepVector.x;
            float newY = bodyComponent.getY() + stepVector.y;
            {
                if (insideMap(newX, bodyComponent.getY()))
                    bodyComponent.getPosition().x = newX;
                if (insideMap(bodyComponent.getX(), newY))
                    bodyComponent.getPosition().y = newY;
            }
        }
        if (!velocityComponent.isConstant() && entity != getPlayer())
            velocityComponent.set(0, 0);*/
    }

}

package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import net.artux.pda.map.engine.components.BodyComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.engine.data.GlobalData;

public class MovingSystem extends BaseSystem {

    private final float MOVEMENT = 150f;
    private final float RUN_MOVEMENT = 30f;
    private final float PLAYER_MULTIPLICATION = 6f;

    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<UserVelocityInput> uvm = ComponentMapper.getFor(UserVelocityInput.class);

    private MapOrientationSystem mapOrientationSystem;

    public static boolean playerWalls = true;
    public static boolean speedup = true;
    public static boolean alwaysRun = false;
    public static boolean playerRunning = false;

    public MovingSystem() {
        super(Family.all(BodyComponent.class).get());
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

            BodyComponent velocityComponent = bm.get(entity);
            Body body = velocityComponent.getBody();
            Vector2 userVelocityInput = uvm.get(entity).getVelocity();


            /*if (speedup)
                userVelocityInput.scl(PLAYER_MULTIPLICATION);
            if (alwaysRun || playerRunning)
                userVelocityInput.scl(RUN_MOVEMENT);*/
            userVelocityInput.scl(200);
            body.applyLinearImpulse(userVelocityInput, body.getPosition(), true);

        }

        /*for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);

            Body body = bm.get(entity).getBody();
            Vector2 positionComponent = body.getPosition();
            Vector2 velocityComponent = body.getLinearVelocity();

            Vector2 stepVector;
            Vector2 currentVelocity = velocityComponent.cpy();

            if (hm.has(entity)) {
                HealthComponent healthComponent = hm.get(entity);

                float staminaDifference = 0;
                if (entity == player && playerRunning && healthComponent.stamina > 0) {
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
            body.applyLinearImpulse(stepVector, body.getPosition(), true);*/

           /* if (!stepVector.isZero()) {
                float newX = positionComponent.x + stepVector.x;
                float newY = positionComponent.y + stepVector.y;
                if (uvm.has(entity) && playerWalls) {
                    *//*
                    if (insideMap(newX, positionComponent.y))
                        positionComponent.getPosition().x += stepVector.x * mapOrientationSystem.getMapBorder().getK(newX, positionComponent.getY());
                    if (insideMap(positionComponent.getX(), newY))
                        positionComponent.getPosition().y += stepVector.y * mapOrientationSystem.getMapBorder().getK(positionComponent.getX(), newY);
                        *//*

                } else {
                    *//*if (insideMap(newX, positionComponent.y))
                        positionComponent.getPosition().x = newX;
                    if (insideMap(positionComponent.getX(), newY))
                        positionComponent.getPosition().y = newY;
                    *//*
                    body.setLinearVelocity(stepVector);
                }
            }*/

            /*if (bm.has(entity)) {
                bm.get(entity).getBody().setTransform(positionComponent.getPosition(), 0);
            }*/
        //}
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public boolean insideMap(float x, float y) {
        return (x <= GlobalData.mapWidth && x >= 0) && (y <= GlobalData.mapHeight && y >= 0);
    }

    public int sign(float v) {
        return v < 0 ? -1 : 1;
    }

}

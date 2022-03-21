package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.pathfinding.TiledNode;

public class MovingSystem extends EntitySystem {

    private float MOVEMENT = 10f;
    private float RUN_MOVEMENT = 20f;
    private float PLAYER_MULTIPLICATION = 6f;

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> players;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<UserVelocityInput> uvm = ComponentMapper.getFor(UserVelocityInput.class);

    private MapOrientationSystem mapOrientationSystem;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(VelocityComponent.class, PositionComponent.class).get());
        players = engine.getEntitiesFor(Family.all(UserVelocityInput.class, VelocityComponent.class, PositionComponent.class).get());

        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);

            VelocityComponent velocityComponent = vm.get(entity);
            UserVelocityInput userVelocityInput = uvm.get(entity);

            velocityComponent.setVelocity(userVelocityInput.getVelocity().scl(PLAYER_MULTIPLICATION));
            velocityComponent.setRunning(userVelocityInput.isRunning());
        }


        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            PositionComponent positionComponent = pm.get(entity);
            VelocityComponent velocityComponent = vm.get(entity);

            Vector2 stepVector;
            if (velocityComponent.isRunning())
                stepVector = velocityComponent.velocity.scl(deltaTime).scl(RUN_MOVEMENT);
            else
                stepVector = velocityComponent.velocity.scl(deltaTime).scl(MOVEMENT);

            if (!stepVector.isZero()){
                float newX = positionComponent.getX() + stepVector.x;
                float newY = positionComponent.getY() + stepVector.y;
                if (uvm.has(entity)) {
                    if (mapOrientationSystem.getMapBorder().getTileType((int) newX, (int) positionComponent.getY()) != TiledNode.TILE_WALL)
                        if (newX <= GlobalData.mapWidth && newX >= 0)
                            positionComponent.getPosition().x = newX;
                    if (mapOrientationSystem.getMapBorder().getTileType((int) positionComponent.getX(), (int) newY) != TiledNode.TILE_WALL)
                        if (newY <= GlobalData.mapHeight && newY >= 0)
                            positionComponent.getPosition().y = newY;
                }else {
                    if (newX <= GlobalData.mapWidth && newX >= 0)
                        positionComponent.getPosition().x = newX;
                    if (newY <= GlobalData.mapHeight && newY >= 0)
                        positionComponent.getPosition().y = newY;
                }
            }

        }
    }
}

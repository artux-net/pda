package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledRaycastCollisionDetector;

public class TargetingSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<TargetMovingComponent> tmm = ComponentMapper.getFor(TargetMovingComponent.class);

    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    MapOrientationSystem mapOrientationSystem;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(VelocityComponent.class, PositionComponent.class, TargetMovingComponent.class).get());

        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);

        heuristic = mapOrientationSystem.heuristic;
        pathFinder = mapOrientationSystem.pathFinder;
        pathSmoother = mapOrientationSystem.pathSmoother;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            PositionComponent positionComponent = pm.get(entity);
            VelocityComponent velocityComponent = vm.get(entity);
            TargetMovingComponent targetMovingComponent = tmm.get(entity);

            if (targetMovingComponent.movementTarget != null) {

                Vector2 target = targetMovingComponent.movementTarget;

                if (mapOrientationSystem.isGraphActive()) {
                    FlatTiledNode startNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(positionComponent.getX(), positionComponent.getY());
                    FlatTiledNode endNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(targetMovingComponent.movementTarget.x, targetMovingComponent.movementTarget.y);
                    if (targetMovingComponent.getPath().nodes.size == 0 || !targetMovingComponent.getPath().nodes.peek().equals(endNode)) {
                        //пути нет и конец не совпадает
                        targetMovingComponent.getPath().clear();
                        targetMovingComponent.tempTarget = null;
                        targetMovingComponent.iterator = null;
                        if (endNode.type != FlatTiledNode.TILE_WALL) {
                            //если конец не стена можем искать
                            pathFinder.searchNodePath(startNode, endNode, heuristic, targetMovingComponent.getPath());
                            pathSmoother.smoothPath(targetMovingComponent.getPath());
                        } else {
                            targetMovingComponent.movementTarget = null;
                        }
                    }else {
                        if (targetMovingComponent.iterator == null)
                            targetMovingComponent.iterator = targetMovingComponent.getPath().iterator();
                        if (targetMovingComponent.tempTarget == null
                                || positionComponent.getPosition().dst(
                                        new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY)) < 5) {
                            if (targetMovingComponent.iterator.hasNext()) {
                                targetMovingComponent.tempTarget = targetMovingComponent.iterator.next();
                                target = new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY);
                            }
                            else {
                                targetMovingComponent.getPath().clear();
                                targetMovingComponent.movementTarget = null;
                                targetMovingComponent.tempTarget = null;
                                targetMovingComponent.iterator = null;
                                target = Vector2.Zero;
                            }
                        }else
                            target = new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY); // движемся к узлу
                    }
                }

                Vector2 unit = new Vector2(target.x - positionComponent.getX(),
                        target.y - positionComponent.getY());

                unit.scl(1 / unit.len());
                velocityComponent.setVelocity(unit);
            } else {
                targetMovingComponent.getPath().clear();
                targetMovingComponent.movementTarget = null;
                targetMovingComponent.tempTarget = null;
                targetMovingComponent.iterator = null;
                velocityComponent.setVelocity(Vector2.Zero);
            }

        }
    }
}

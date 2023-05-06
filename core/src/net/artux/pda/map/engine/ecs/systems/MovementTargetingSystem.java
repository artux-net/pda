package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

import net.artux.engine.pathfinding.FlatTiledNode;
import net.artux.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.GraphMotionComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class MovementTargetingSystem extends BaseSystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);

    private final TiledManhattanDistance<FlatTiledNode> heuristic;
    private final IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    private final PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    private final MapOrientationSystem mapOrientationSystem;
    private final Vector2 tempUnit = new Vector2();

    @Inject
    public MovementTargetingSystem(MapOrientationSystem mapOrientationSystem) {
        super(Family.all(BodyComponent.class, GraphMotionComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;

        heuristic = mapOrientationSystem.getHeuristic();
        pathFinder = mapOrientationSystem.getPathFinder();
        pathSmoother = mapOrientationSystem.getPathSmoother();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent bodyComponent = pm.get(entity);
        GraphMotionComponent targetMovingComponent = gmm.get(entity);

        if (targetMovingComponent.isActive()) {
            Vector2 target = targetMovingComponent.movementTarget;

            if (mapOrientationSystem.isGraphActive()) {
                FlatTiledNode startNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(bodyComponent.getX(), bodyComponent.getY());
                FlatTiledNode endNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(targetMovingComponent.movementTarget.x, targetMovingComponent.movementTarget.y);
                if (targetMovingComponent.getPath().nodes.size == 0 || !targetMovingComponent.getPath().nodes.peek().equals(endNode)) {
                    //пути нет и конец не совпадает
                    targetMovingComponent.reset();
                    if (endNode.type.isWalkable()) {
                        //если конец не стена можем искать
                        pathFinder.searchNodePath(startNode, endNode, heuristic, targetMovingComponent.getPath());
                        pathSmoother.smoothPath(targetMovingComponent.getPath());
                    } else {
                        targetMovingComponent.movementTarget = null;
                    }
                } else {
                    if (targetMovingComponent.iterator == null)
                        targetMovingComponent.iterator = targetMovingComponent.getPath().iterator();
                    if (targetMovingComponent.tempTarget == null
                            || bodyComponent.getPosition().dst(
                            new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY)) < 5) {
                        if (targetMovingComponent.iterator.hasNext()) {
                            targetMovingComponent.tempTarget = targetMovingComponent.iterator.next();
                            target = new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY);
                        } else {
                            targetMovingComponent.getPath().clear();
                            targetMovingComponent.movementTarget = null;
                            targetMovingComponent.tempTarget = null;
                            targetMovingComponent.iterator = null;
                            target = Vector2.Zero;
                        }
                    } else
                        target = new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY); // движемся к узлу
                }
            }

            tempUnit.set(target.x - bodyComponent.getX(),
                    target.y - bodyComponent.getY()).nor();

            if (!tempUnit.isZero()) {
                bodyComponent.getBody().applyLinearImpulse(
                        tempUnit.x * bodyComponent.getMovementForce(),
                        tempUnit.y * bodyComponent.getMovementForce(),
                        bodyComponent.getPosition().x,
                        bodyComponent.getPosition().y,
                        true
                );
            }
        }
    }

}

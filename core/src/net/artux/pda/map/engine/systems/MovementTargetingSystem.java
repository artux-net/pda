package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.core.PerGameMap;
import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;

import javax.inject.Inject;

@PerGameMap
public class MovementTargetingSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);

    private TiledManhattanDistance<FlatTiledNode> heuristic;
    private IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    private PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    MapOrientationSystem mapOrientationSystem;

    @Inject
    public MovementTargetingSystem(MapOrientationSystem mapOrientationSystem) {
        super(Family.all(VelocityComponent.class, PositionComponent.class, GraphMotionComponent.class).get());
        this.mapOrientationSystem = mapOrientationSystem;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        heuristic = mapOrientationSystem.getHeuristic();
        pathFinder = mapOrientationSystem.getPathFinder();
        pathSmoother = mapOrientationSystem.getPathSmoother();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);

            PositionComponent positionComponent = pm.get(entity);
            VelocityComponent velocityComponent = vm.get(entity);
            GraphMotionComponent targetMovingComponent = gmm.get(entity);

            if (targetMovingComponent.isActive()) {

                Vector2 target = targetMovingComponent.movementTarget;

                if (mapOrientationSystem.isGraphActive()) {
                    FlatTiledNode startNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(positionComponent.getX(), positionComponent.getY());
                    FlatTiledNode endNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(targetMovingComponent.movementTarget.x, targetMovingComponent.movementTarget.y);
                    if (targetMovingComponent.getPath().nodes.size == 0 || !targetMovingComponent.getPath().nodes.peek().equals(endNode)) {
                        //пути нет и конец не совпадает
                        targetMovingComponent.reset();
                        if (endNode.type != FlatTiledNode.TILE_WALL) {
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
                                || positionComponent.getPosition().dst(
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

                Vector2 unit = new Vector2(target.x - positionComponent.getX(),
                        target.y - positionComponent.getY());

                unit.scl(1 / unit.len());
                velocityComponent.setVelocity(unit);
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}

package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledSmoothableGraphPath;
import net.artux.pda.map.engine.systems.MapOrientationSystem;

import java.util.Iterator;

public class TargetMovingComponent implements Component {
    public Targeting targeting;
    public Vector2 movementTarget;

    public FlatTiledNode tempTarget;
    TiledSmoothableGraphPath<FlatTiledNode> path;
    public Iterator<FlatTiledNode> iterator;

    private MapOrientationSystem mapOrientationSystem;

    public TargetMovingComponent(Targeting targeting) {
        this.targeting = targeting;
        path = new TiledSmoothableGraphPath<FlatTiledNode>();
    }

    public void setPathToNextTarget(Vector2 from){
        setMovementTarget(targeting.getTarget());
        findPath(from);
    }

    private void findPath(Vector2 currentPosition) {
        if (mapOrientationSystem != null) {
            PathFinder pathFinder = mapOrientationSystem.getPathFinder();
            PathSmoother pathSmoother = mapOrientationSystem.getPathSmoother();
            TiledManhattanDistance heuristic = mapOrientationSystem.getHeuristic();

            if (mapOrientationSystem.isGraphActive()) {
                FlatTiledNode startNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(currentPosition.x, currentPosition.y);
                FlatTiledNode endNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(movementTarget.x, movementTarget.y);
                if (path.nodes.size == 0 || !path.nodes.peek().equals(endNode)) {
                    //пути нет и конец не совпадает
                    path.clear();
                    tempTarget = null;
                    iterator = null;
                    if (endNode.type != FlatTiledNode.TILE_WALL) {
                        //если конец не стена можем искать
                        pathFinder.searchNodePath(startNode, endNode, heuristic, getPath());
                        pathSmoother.smoothPath(getPath());
                        iterator = getPath().iterator();
                    } else {
                        movementTarget = null;
                    }
                }
            }
        }
    }

    public void setMapOrientationSystem(MapOrientationSystem mapOrientationSystem) {
        this.mapOrientationSystem = mapOrientationSystem;
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setMovementTarget(Vector2 movementTarget) {
        this.movementTarget = movementTarget;
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return path;
    }

    public interface Targeting {

        Vector2 getTarget();

    }

}
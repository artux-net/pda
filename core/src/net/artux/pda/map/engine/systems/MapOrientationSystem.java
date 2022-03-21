package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.MapBorder;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledRaycastCollisionDetector;

public class MapOrientationSystem extends EntitySystem {

    private FlatTiledGraph worldGraph;
    private final MapBorder mapBorder;

    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    TiledRaycastCollisionDetector<FlatTiledNode> collisionDetector;

    public MapOrientationSystem(MapBorder mapBorder) {
        this.mapBorder = mapBorder;
        if (mapBorder.isMobTilesActive()) {
            this.worldGraph = new FlatTiledGraph(mapBorder);
            heuristic = new TiledManhattanDistance<>();
            pathFinder = new IndexedAStarPathFinder<>(worldGraph, true);
            collisionDetector = new TiledRaycastCollisionDetector<>(worldGraph);
            pathSmoother = new PathSmoother<>(collisionDetector);
        }
    }

    public TiledRaycastCollisionDetector<FlatTiledNode> getCollisionDetector() {
        return collisionDetector;
    }

    public PathSmoother<FlatTiledNode, Vector2> getPathSmoother() {
        return pathSmoother;
    }

    public FlatTiledGraph getWorldGraph() {
        return worldGraph;
    }

    public MapBorder getMapBorder() {
        return mapBorder;
    }

    public boolean isGraphActive(){
        return worldGraph != null;
    }
}

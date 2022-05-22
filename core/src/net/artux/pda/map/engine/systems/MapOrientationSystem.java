package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.MapBorders;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledRaycastCollisionDetector;
import net.artux.pda.map.model.Map;

public class MapOrientationSystem extends EntitySystem implements Disposable {

    private FlatTiledGraph worldGraph;
    private final MapBorders mapBorders;

    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    TiledRaycastCollisionDetector<FlatTiledNode> collisionDetector;

    public MapOrientationSystem(AssetsFinder assetsFinder, Map map) {
        this.mapBorders = new MapBorders(assetsFinder.getLocal(map.getTilesTexture()), assetsFinder.getLocal(map.getBoundsTextureUri()));
        if (mapBorders.isMobTilesActive()) {
            this.worldGraph = new FlatTiledGraph(mapBorders);
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

    public MapBorders getMapBorder() {
        return mapBorders;
    }

    public boolean isGraphActive(){
        return worldGraph != null;
    }

    public MapBorders getMapBorders() {
        return mapBorders;
    }

    @Override
    public void dispose() {
        mapBorders.dispose();
    }
}

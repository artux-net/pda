package net.artux.pda.map.engine.systems;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.engine.pathfinding.TiledNode.TILE_WALL;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.MapBorder;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledRaycastCollisionDetector;
import net.artux.pda.model.map.GameMap;

public class MapOrientationSystem extends EntitySystem implements Disposable {

    private FlatTiledGraph worldGraph;
    private final MapBorder mapBorder;

    private TiledManhattanDistance<FlatTiledNode> heuristic;
    private IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    private PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    private TiledRaycastCollisionDetector<FlatTiledNode> collisionDetector;
    private final Ray<Vector2> ray;

    public MapOrientationSystem(AssetsFinder assetsFinder, GameMap map) {
        ray = new Ray<>(Vector2.Zero, Vector2.Zero);
        this.mapBorder = new MapBorder(assetsFinder, map);

        if (mapBorder.isMobTilesActive()) {
            this.worldGraph = new FlatTiledGraph(mapBorder);
            heuristic = new TiledManhattanDistance<>();
            pathFinder = new IndexedAStarPathFinder<>(worldGraph, true);
            collisionDetector = new TiledRaycastCollisionDetector<>(worldGraph);
            pathSmoother = new PathSmoother<>(collisionDetector);
        }
    }

    public TiledManhattanDistance<FlatTiledNode> getHeuristic() {
        return heuristic;
    }

    public IndexedAStarPathFinder<FlatTiledNode> getPathFinder() {
        return pathFinder;
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

    public Vector2 getRandomFreePoint(Camera camera) {
        int x;
        int y;
        Vector2 result;
        if (mapBorder.isMobTilesActive()) {
            do {
                x = random.nextInt(worldGraph.sizeX);
                y = random.nextInt(worldGraph.sizeY);
                result = getRandomVectorWithInNode(x, y);
            } while (worldGraph.getNode(x, y).type == TILE_WALL || camera.frustum.pointInFrustum(result.x, result.y, 0));
        } else {
            result = new Vector2(random.nextInt(mapBorder.getMapWidth()), random.nextInt(mapBorder.getMapHeight()));
        }
        return result;
    }

    private Vector2 getRandomVectorWithInNode(int x, int y) {
        return new Vector2(x * FlatTiledGraph.tileSize + random.nextInt(FlatTiledGraph.tileSize), y * FlatTiledGraph.tileSize + random.nextInt(FlatTiledGraph.tileSize));
    }

    public MapBorder getMapBorder() {
        return mapBorder;
    }

    public boolean isGraphActive() {
        return worldGraph != null;
    }

    public MapBorder getMapBorders() {
        return mapBorder;
    }

    @Override
    public void dispose() {
        worldGraph.dispose();
        mapBorder.dispose();
    }

    private boolean isInsideMap(Vector2 position) {
        return position.x >= 0 && position.y >= 0 && position.x <= mapBorder.getMapWidth() && position.y <= mapBorder.getMapHeight();
    }

    public boolean collides(Vector2 start, Vector2 end) {
        if (isGraphActive() && isInsideMap(start) && isInsideMap(end)) {
            FlatTiledNode startNode = getWorldGraph().getNodeInPosition(start);
            FlatTiledNode endNode = getWorldGraph().getNodeInPosition(end);

            ray.set(startNode.getPosition(), endNode.getPosition());
            return collisionDetector.collides(ray);
        }
        return false;
    }
}

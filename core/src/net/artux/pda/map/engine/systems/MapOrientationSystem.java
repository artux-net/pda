package net.artux.pda.map.engine.systems;

import static net.artux.pda.map.engine.pathfinding.TiledNode.TILE_WALL;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.MapBorders;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledRaycastCollisionDetector;
import net.artux.pda.map.model.input.GameMap;

import java.util.Random;

public class MapOrientationSystem extends EntitySystem implements Disposable {

    private FlatTiledGraph worldGraph;
    private final MapBorders mapBorders;
    private Random random = new Random();

    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    TiledRaycastCollisionDetector<FlatTiledNode> collisionDetector;

    public MapOrientationSystem(AssetsFinder assetsFinder, GameMap map) {
        this.mapBorders = new MapBorders(assetsFinder.getLocal(map.getTilesTexture()),
                assetsFinder.getLocal(map.getBoundsTextureUri()));
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

    public Vector2 getRandomFreePoint(Camera camera) {
        int x;
        int y;
        Vector2 result;
        if (worldGraph != null) {
            do {
                x = random.nextInt(worldGraph.sizeX);
                y = random.nextInt(worldGraph.sizeY);
                result = getRandomVectorWithInNode(x, y);
            } while (worldGraph.getNode(x, y).type == TILE_WALL || camera.frustum.pointInFrustum(result.x, result.y, 0));
        } else {
            result = new Vector2(random.nextInt(mapBorders.getWidth()), random.nextInt(mapBorders.getHeight()));
        }
        return result;
    }

    private Vector2 getRandomVectorWithInNode(int x, int y) {
        return new Vector2(x * FlatTiledGraph.tileSize + random.nextInt(FlatTiledGraph.tileSize), y * FlatTiledGraph.tileSize + random.nextInt(FlatTiledGraph.tileSize));
    }

    public MapBorders getMapBorder() {
        return mapBorders;
    }

    public boolean isGraphActive() {
        return worldGraph != null;
    }

    public MapBorders getMapBorders() {
        return mapBorders;
    }

    @Override
    public void dispose() {
        worldGraph.dispose();
        mapBorders.dispose();
    }
}

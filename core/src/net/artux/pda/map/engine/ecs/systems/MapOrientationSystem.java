package net.artux.pda.map.engine.ecs.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import net.artux.engine.pathfinding.FlatTiledGraph;
import net.artux.engine.pathfinding.FlatTiledNode;
import net.artux.engine.pathfinding.TiledManhattanDistance;
import net.artux.engine.pathfinding.TiledNavigator;
import net.artux.engine.pathfinding.TiledRaycastCollisionDetector;
import net.artux.pda.map.engine.ecs.systems.player.CameraSystem;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import javax.inject.Inject;

import dagger.Provides;

@PerGameMap
public class MapOrientationSystem extends EntitySystem {

    private final FlatTiledGraph worldGraph;
    private final TiledNavigator tiledNavigator;

    private final TiledManhattanDistance<FlatTiledNode> heuristic;
    private final IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    private final PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    private final TiledRaycastCollisionDetector<FlatTiledNode> collisionDetector;
    private final Ray<Vector2> ray;
    private final int tileSize;
    private final Camera camera;

    @Inject
    public MapOrientationSystem(TiledNavigator tiledNavigator, CameraSystem cameraSystem) {
        ray = new Ray<>(Vector2.Zero, Vector2.Zero);
        this.tiledNavigator = tiledNavigator;
        tileSize = tiledNavigator.getLayer().getTileHeight();
        camera = cameraSystem.getCamera();

        this.worldGraph = new FlatTiledGraph(tiledNavigator);
        heuristic = new TiledManhattanDistance<>();
        pathFinder = new IndexedAStarPathFinder<>(worldGraph, true);
        collisionDetector = new TiledRaycastCollisionDetector<>(worldGraph);
        pathSmoother = new PathSmoother<>(collisionDetector);
    }

    public TiledManhattanDistance<FlatTiledNode> getHeuristic() {
        return heuristic;
    }

    public IndexedAStarPathFinder<FlatTiledNode> getPathFinder() {
        return pathFinder;
    }

    public PathSmoother<FlatTiledNode, Vector2> getPathSmoother() {
        return pathSmoother;
    }

    public FlatTiledGraph getWorldGraph() {
        return worldGraph;
    }

    public Vector2 getRandomFreePoint() {
        int x;
        int y;
        Vector2 result;
        do {
            x = random.nextInt(worldGraph.sizeX);
            y = random.nextInt(worldGraph.sizeY);
            result = getRandomVectorWithInNode(x, y);
        } while (!worldGraph.getNode(x, y).type.isWalkable() ||
                camera.frustum.pointInFrustum(result.x, result.y, 0));
        return result;
    }

    private Vector2 getRandomVectorWithInNode(int x, int y) {
        return new Vector2(x * tileSize + random.nextInt(tileSize),
                y * tileSize + random.nextInt(tileSize));
    }

    public TiledNavigator getNavigator() {
        return tiledNavigator;
    }

    public boolean collides(Vector2 start, Vector2 end) {
        FlatTiledNode startNode = getWorldGraph().getNodeInPosition(start);
        FlatTiledNode endNode = getWorldGraph().getNodeInPosition(end);

        ray.set(startNode.getPosition(), endNode.getPosition());
        return collisionDetector.collides(ray);
    }
}

package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.di.core.PerGameMap;
import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.TiledSmoothableGraphPath;

import javax.inject.Inject;

@PerGameMap
public class MapLoggerSystem extends IteratingSystem implements Drawable, Disposable {

    private final ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);

    private final ShapeRenderer sr;

    private MapOrientationSystem mapOrientationSystem;

    public static boolean showPlayerWalls = false;
    public static boolean showTiles = false;
    public static boolean showPaths = false;

    @Inject
    public MapLoggerSystem(MapOrientationSystem mapOrientationSystem) {
        super(Family.all(GraphMotionComponent.class).get());
        sr = new ShapeRenderer();
        this.mapOrientationSystem = mapOrientationSystem;
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    @Override
    public void dispose() {
        sr.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (showPlayerWalls) {
            batch.draw(mapOrientationSystem.getMapBorder().getPlayerLayout(), 0, 0);
        }

        batch.end();
        sr.setProjectionMatrix(batch.getProjectionMatrix());
        if (showTiles) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            for (int x = 0; x < mapOrientationSystem.getWorldGraph().sizeX; x++) {
                for (int y = 0; y < mapOrientationSystem.getWorldGraph().sizeY; y++) {
                    switch (mapOrientationSystem.getWorldGraph().getNode(x, y).type) {
                        case FlatTiledNode.TILE_WALL:
                            sr.setColor(Color.RED);
                            sr.rect(x * FlatTiledGraph.tileSize, y * FlatTiledGraph.tileSize, FlatTiledGraph.tileSize, FlatTiledGraph.tileSize);
                            break;
                        case FlatTiledNode.TILE_ROAD:
                            sr.setColor(Color.BLUE);
                            sr.rect(x * FlatTiledGraph.tileSize, y * FlatTiledGraph.tileSize, FlatTiledGraph.tileSize, FlatTiledGraph.tileSize);
                            break;
                        case FlatTiledNode.TILE_GRASS:
                            sr.setColor(Color.GREEN);
                            sr.rect(x * FlatTiledGraph.tileSize, y * FlatTiledGraph.tileSize, FlatTiledGraph.tileSize, FlatTiledGraph.tileSize);
                            break;
                    }

                }
            }
            sr.end();
        }

        if (showPaths) {
            sr.setColor(Color.ORANGE);

            for (int i = 0; i < getEntities().size(); i++) {
                Entity entity = getEntities().get(i);

                GraphMotionComponent graphMotionComponent = gmm.get(entity);
                TiledSmoothableGraphPath<FlatTiledNode> graph = graphMotionComponent.getPath();
                if (graph != null) {
                    int nodeCount = graph.getCount();

                    sr.setProjectionMatrix(batch.getProjectionMatrix());
                    sr.begin(ShapeRenderer.ShapeType.Filled);
                    for (int j = 0; j < nodeCount; j++) {
                        FlatTiledNode node = graph.nodes.get(j);
                        if (j == nodeCount - 1)
                            sr.rect(node.realX, node.realY, 4, 4);
                        else
                            sr.circle(node.realX, node.realY, 2);
                    }
                    sr.end();

                    sr.setProjectionMatrix(batch.getProjectionMatrix());
                    sr.begin(ShapeRenderer.ShapeType.Line);
                    if (nodeCount > 0) {
                        FlatTiledNode prevNode = graph.nodes.get(0);
                        for (int j = 1; j < nodeCount; j++) {
                            FlatTiledNode node = graph.nodes.get(j);
                            sr.line(node.realX, node.realY, prevNode.realX, prevNode.realY);
                            prevNode = node;
                        }
                    }
                    sr.end();
                }
            }
        }
        batch.begin();
    }
}

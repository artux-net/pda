package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.TiledSmoothableGraphPath;

public class MapLoggerSystem extends EntitySystem implements Disposable {

    private Array<Entity> entities;
    private Batch batch;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TargetMovingComponent> tm = ComponentMapper.getFor(TargetMovingComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private ShapeRenderer sr = new ShapeRenderer();

    private MapOrientationSystem mapOrientationSystem;

    public static boolean showPlayerWalls = false;
    public static boolean showTiles = false;
    public static boolean showPaths = false;

    public MapLoggerSystem(Batch batch) {
        this.batch = batch;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = new Array<>(engine.getEntitiesFor(Family.all(TargetMovingComponent.class).get()).toArray());

       /* engine.addEntityListener(Family.all(HealthComponent.class, PositionComponent.class, WeaponComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                entities.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                entities.removeValue(entity, true);
            }
        });*/

        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    public void drawObjects(float delta) {

        sr.setProjectionMatrix(batch.getProjectionMatrix());

        if (showPlayerWalls){
            batch.begin();
            batch.draw(mapOrientationSystem.getMapBorder().getPlayerLayout(), 0, 0);
            batch.end();
        }

        if (showTiles) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            for (int x = 0; x < mapOrientationSystem.getWorldGraph().sizeX; x++) {
                for (int y = 0; y < mapOrientationSystem.getWorldGraph().sizeY; y++) {
                    switch (mapOrientationSystem.getWorldGraph().getNode(x, y).type) {
                        /*case FlatTiledNode.TILE_EMPTY:
                            sr.setColor(Color.WHITE);
                            break;*/
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

            for (int i = 0; i < entities.size; i++) {
                Entity entity = entities.get(i);

                TargetMovingComponent targetMovingComponent = tm.get(entity);
                PositionComponent positionComponent = pm.get(entity);

                TiledSmoothableGraphPath<FlatTiledNode> graph = targetMovingComponent.getPath();
                if (graph != null) {
                    int nodeCount = graph.getCount();

                    int width = FlatTiledGraph.tileSize;

                    sr.setProjectionMatrix(batch.getProjectionMatrix());
                    sr.begin(ShapeRenderer.ShapeType.Filled);
                    for (int j = 0; j < nodeCount; j++) {
                        FlatTiledNode node = graph.nodes.get(j);
                        sr.rect(node.realX, node.realY, 2, 2);
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
    }

    @Override
    public void dispose() {
        sr.dispose();
    }
}

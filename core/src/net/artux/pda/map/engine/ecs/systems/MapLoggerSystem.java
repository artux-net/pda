package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import net.artux.engine.pathfinding.FlatTiledNode;
import net.artux.engine.pathfinding.TiledSmoothableGraphPath;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.GraphMotionComponent;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class MapLoggerSystem extends IteratingSystem implements Drawable, Disposable {

    private final ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);

    private final ShapeRenderer sr;

    private MapOrientationSystem mapOrientationSystem;
    private TiledMap tiledMap;
    private World world;
    private Stage stage;
    private final Box2DDebugRenderer boxDebugRenderer;

    public static boolean showPlayerWalls = false;
    public static boolean showPaths = false;

    @Inject
    public MapLoggerSystem(MapOrientationSystem mapOrientationSystem, TiledMap tiledMap, World world, @Named("gameStage") Stage stage) {
        super(Family.all(GraphMotionComponent.class).get(), -100);
        this.world = world;
        this.stage = stage;
        this.tiledMap = tiledMap;
        sr = new ShapeRenderer();
        boxDebugRenderer = new Box2DDebugRenderer();
        this.mapOrientationSystem = mapOrientationSystem;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public void debugTiledMap(boolean debug) {
        if (debug) {
            for (MapLayer layer : tiledMap.getLayers()) {
                layer.setVisible(true);
                layer.setOpacity(0.5f);
            }
        } else {
            for (MapLayer layer : tiledMap.getLayers()) {
                layer.setOpacity(1f);
            }
            tiledMap.getLayers().get("tiles").setVisible(false);
        }
    }

    @Override
    public void dispose() {
        boxDebugRenderer.dispose();
        sr.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (showPlayerWalls) {
            boxDebugRenderer.render(world, stage.getCamera().combined);
        }

        batch.end();
        sr.setProjectionMatrix(batch.getProjectionMatrix());

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

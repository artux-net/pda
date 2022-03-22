package net.artux.tests.pathfind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.engine.pathfinding.FlatTiledGraph;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.MapBorders;
import net.artux.pda.map.engine.pathfinding.TiledManhattanDistance;
import net.artux.pda.map.engine.pathfinding.TiledRaycastCollisionDetector;
import net.artux.pda.map.engine.pathfinding.TiledSmoothableGraphPath;
import net.artux.pda.map.model.Map;

public class PathFinderTest extends PathFinderTestBase {

    static float width = 8; // 5; // 10;

    ShapeRenderer renderer;
    Vector3 tmpUnprojection = new Vector3();

    int lastScreenX;
    int lastScreenY;
    int lastEndTileX;
    int lastEndTileY;
    int startTileX;
    int startTileY;

    FlatTiledGraph worldMap;
    TiledSmoothableGraphPath<FlatTiledNode> path;
    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;

    boolean smooth = false;

    CheckBox checkDiagonal;
    CheckBox checkSmooth;
    CheckBox checkMetrics;

    Map map;
    private String cachePath = "data/";

    public PathFinderTest (PathFinderAdapter container, Map map) {
        super(container, "Flat Tiled A*");
        this.map = map;
    }

    @Override
    public void create () {
        Texture texture =  new Texture(Gdx.files.local(cachePath + map.getTextureUri()));;
        Texture bounds =  new Texture(Gdx.files.local(cachePath + map.getBoundsTextureUri()));;
        MapBorders mapBorders = new MapBorders(bounds, null);
        // Create the map
        worldMap = new FlatTiledGraph(mapBorders);
        width = Gdx.graphics.getWidth()/(mapBorders.getHeight()/16f);

        path = new TiledSmoothableGraphPath<FlatTiledNode>();
        heuristic = new TiledManhattanDistance<FlatTiledNode>();
        pathFinder = new IndexedAStarPathFinder<FlatTiledNode>(worldMap, true);
        TiledRaycastCollisionDetector collisionDetector = new TiledRaycastCollisionDetector<FlatTiledNode>(worldMap);
        pathSmoother = new PathSmoother<FlatTiledNode, Vector2>(collisionDetector);

        renderer = new ShapeRenderer();
        inputProcessor = new TiledAStarInputProcessor(this);

        Table detailTable = new Table(container.skin);

        detailTable.row();
        checkSmooth = new CheckBox("[RED]S[]mooth Path", container.skin);
        checkSmooth.setChecked(smooth);
        checkSmooth.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                CheckBox checkBox = (CheckBox)event.getListenerActor();
                smooth = checkBox.isChecked();
                updatePath(true);
                System.out.println("smooth" + smooth);
            }
        });
        detailTable.add(checkSmooth);

        detailTable.row();
        checkDiagonal = new CheckBox("Prefer [RED]D[]iagonal", container.skin);
        checkDiagonal.setChecked(worldMap.diagonal);
        checkDiagonal.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                CheckBox checkBox = (CheckBox)event.getListenerActor();
                worldMap.diagonal = checkBox.isChecked();
                updatePath(true);
                System.out.println("diagonal" + worldMap.diagonal);
            }
        });
        detailTable.add(checkDiagonal);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        checkMetrics = new CheckBox("Calculate [RED]M[]etrics", container.skin);
        checkMetrics.setChecked(pathFinder.metrics != null);
        checkMetrics.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                CheckBox checkBox = (CheckBox)event.getListenerActor();
                pathFinder.metrics = checkBox.isChecked() ? new IndexedAStarPathFinder.Metrics() : null;
                updatePath(true);
            }
        });
        detailTable.add(checkMetrics);

        detailWindow = createDetailWindow(detailTable);

    }

    @Override
    public void render () {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < worldMap.sizeX; x++) {
            for (int y = 0; y < worldMap.sizeY; y++) {
                switch (worldMap.getNode(x, y).type) {
                    case FlatTiledNode.TILE_EMPTY:
                        renderer.setColor(Color.WHITE);
                        break;
                    case FlatTiledNode.TILE_WALL:
                        renderer.setColor(Color.RED);
                        break;
                    case FlatTiledNode.TILE_ROAD:
                        renderer.setColor(Color.BLUE);
                    case FlatTiledNode.TILE_GRASS:
                        renderer.setColor(Color.GREEN);
                        break;
                    default:
                        renderer.setColor(Color.BLACK);
                        break;
                }
                renderer.rect(x * width, y * width, width, width);
            }
        }

        renderer.setColor(Color.ORANGE);
        int nodeCount = path.getCount();

        for (int i = 0; i < nodeCount; i++) {
            FlatTiledNode node = path.nodes.get(i);
            renderer.rect(node.x * width, node.y * width, width, width);
        }

        if (smooth) {
            renderer.end();
            renderer.begin(ShapeRenderer.ShapeType.Line);
            float hw = width / 2f;
            if (nodeCount > 0) {
                FlatTiledNode prevNode = path.nodes.get(0);
                for (int i = 1; i < nodeCount; i++) {
                    FlatTiledNode node = path.nodes.get(i);
                    renderer.line(node.x * width + hw, node.y * width + hw, prevNode.x * width + hw, prevNode.y * width + hw);
                    prevNode = node;
                }
            }
        }
        renderer.end();
    }

    @Override
    public void dispose () {
        renderer.dispose();

        worldMap = null;
        path = null;
        heuristic = null;
        pathFinder = null;
        pathSmoother = null;
    }

    public Camera getCamera () {
        return container.stage.getViewport().getCamera();
    }

    private void updatePath (boolean forceUpdate) {
        getCamera().unproject(tmpUnprojection.set(lastScreenX, lastScreenY, 0));
        int tileX = (int)(tmpUnprojection.x / width);
        int tileY = (int)(tmpUnprojection.y / width);
        if ((forceUpdate || tileX != lastEndTileX || tileY != lastEndTileY) ) { //&& tileX < worldMap.sizeX*width && tileY < worldMap.sizeY*width
            FlatTiledNode startNode = worldMap.getNode(startTileX, startTileY);
            FlatTiledNode endNode = worldMap.getNode(tileX, tileY);
            if (forceUpdate || endNode.type != FlatTiledNode.TILE_WALL) {
                if (endNode.type != FlatTiledNode.TILE_WALL) {
                    lastEndTileX = tileX;
                    lastEndTileY = tileY;
                } else {
                    endNode = worldMap.getNode(lastEndTileX, lastEndTileY);
                }
                path.clear();
                worldMap.startNode = startNode;
                long startTime = nanoTime();
                pathFinder.searchNodePath(startNode, endNode, heuristic, path);
                if (pathFinder.metrics != null) {
                    float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
                    System.out.println("----------------- Indexed A* Path Finder Metrics -----------------");
                    System.out.println("Visited nodes................... = " + pathFinder.metrics.visitedNodes);
                    System.out.println("Open list additions............. = " + pathFinder.metrics.openListAdditions);
                    System.out.println("Open list peak.................. = " + pathFinder.metrics.openListPeak);
                    System.out.println("Path finding elapsed time (ms).. = " + elapsed);
                }
                if (smooth) {
                    startTime = nanoTime();
                    pathSmoother.smoothPath(path);
                    if (pathFinder.metrics != null) {
                        float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
                        System.out.println("Path smoothing elapsed time (ms) = " + elapsed);
                    }
                }
            }
        }
    }

    private long nanoTime () {
        return pathFinder.metrics == null ? 0 : TimeUtils.nanoTime();
    }

    /** An {@link InputProcessor} that allows you to define a path to find.
     *
     * @autor davebaol */
    static class TiledAStarInputProcessor extends InputAdapter {
        PathFinderTest test;

        public TiledAStarInputProcessor (PathFinderTest test) {
            this.test = test;
        }

        @Override
        public boolean keyTyped (char character) {
            switch (character) {
                case 'm':
                case 'M':
                    test.checkMetrics.toggle();
                    break;
                case 'd':
                case 'D':
                    test.checkDiagonal.toggle();
                    break;
                case 's':
                case 'S':
                    test.checkSmooth.toggle();
                    break;
            }
            return true;
        }

        @Override
        public boolean touchUp (int screenX, int screenY, int pointer, int button) {
            test.getCamera().unproject(test.tmpUnprojection.set(screenX, screenY, 0));
            int tileX = (int)(test.tmpUnprojection.x / width);
            int tileY = (int)(test.tmpUnprojection.y / width);
            FlatTiledNode startNode = test.worldMap.getNode(tileX, tileY);
            if (startNode.type != FlatTiledNode.TILE_WALL) {
                test.startTileX = tileX;
                test.startTileY = tileY;
                test.updatePath(true);
            }
            return true;
        }

        @Override
        public boolean mouseMoved (int screenX, int screenY) {
            test.lastScreenX = screenX;
            test.lastScreenY = screenY;
            test.updatePath(false);
            return true;
        }
    }
}

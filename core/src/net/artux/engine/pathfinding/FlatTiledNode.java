package net.artux.engine.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ai.TileType;

public class FlatTiledNode extends TiledNode<FlatTiledNode> {

    FlatTiledGraph graph;
    public int realX;
    public int realY;

    private final Vector2 position = new Vector2();

    public FlatTiledNode(FlatTiledGraph graph, int x, int y, int realX, int realY, TileType type, int connectionCapacity) {
        super(x, y, type, new Array<>(connectionCapacity));
        this.realX = realX;
        this.realY = realY;
        this.graph = graph;
    }

    public Vector2 getPosition() {
        position.set(x, y);
        return position;
    }

    public Vector2 getRealPosition() {
        position.set(realX, realY);
        return position;
    }


    @Override
    public int getIndex() {
        return x * graph.sizeY + y;
    }

}

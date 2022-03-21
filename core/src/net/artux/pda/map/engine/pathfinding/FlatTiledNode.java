package net.artux.pda.map.engine.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class FlatTiledNode extends TiledNode<FlatTiledNode> {

    FlatTiledGraph graph;
    public int realX;
    public int realY;

    public FlatTiledNode (FlatTiledGraph graph, int x, int y, int realX, int realY, int type, int connectionCapacity) {
        super(x, y, type, new Array<Connection<FlatTiledNode>>(connectionCapacity));
        this.realX = realX;
        this.realY = realY;
        this.graph = graph;
    }

    @Override
    public int getIndex () {
        return x * graph.sizeY + y;
    }

}

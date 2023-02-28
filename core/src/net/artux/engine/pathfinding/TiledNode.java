package net.artux.engine.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ai.TileType;

public abstract class TiledNode<N extends TiledNode<N>> {

    /**
     * The x coordinate of this tile
     */
    public final int x;

    /**
     * The y coordinate of this tile
     */
    public final int y;

    public final TileType type;

    protected Array<Connection<N>> connections;

    public TiledNode(int x, int y, TileType type, Array<Connection<N>> connections) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.connections = connections;
    }

    public abstract int getIndex();

    public Array<Connection<N>> getConnections() {
        return this.connections;
    }

}

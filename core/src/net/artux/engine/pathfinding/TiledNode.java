package net.artux.engine.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public abstract class TiledNode<N extends TiledNode<N>> {

    /** A constant representing an empty tile */
    public static final int TILE_EMPTY = 1;

    /** A constant representing a walkable tile */
    public static final int TILE_ROAD = 0;
    public static final int TILE_GRASS = 3;
    public static final int TILE_SWAMP = 4;

    /** A constant representing a wall */
    public static final int TILE_WALL = 5;

    /** The x coordinate of this tile */
    public final int x;

    /** The y coordinate of this tile */
    public final int y;

    /** The type of this tile, see {@link #TILE_EMPTY}, {@link #TILE_ROAD} and {@link #TILE_WALL} */
    public final int type;

    protected Array<Connection<N>> connections;

    public TiledNode (int x, int y, int type, Array<Connection<N>> connections) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.connections = connections;
    }

    public abstract int getIndex ();

    public Array<Connection<N>> getConnections () {
        return this.connections;
    }

}

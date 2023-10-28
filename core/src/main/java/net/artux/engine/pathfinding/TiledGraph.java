package net.artux.engine.pathfinding;

import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;

public interface TiledGraph<N extends TiledNode<N>> extends IndexedGraph<N> {


    N getNode (int x, int y);

    N getNode (int index);

}

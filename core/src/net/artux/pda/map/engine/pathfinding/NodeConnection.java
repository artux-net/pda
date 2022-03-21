package net.artux.pda.map.engine.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;

public class NodeConnection<N> implements Connection<N> {

    protected N fromNode;
    protected N toNode;
    protected float cost;
    static final float NON_DIAGONAL_COST = (float)Math.sqrt(2);

    public NodeConnection (N fromNode, N toNode, float cost) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.cost = cost;
    }

    public float getCost () {
        return cost;
    }

    @Override
    public N getFromNode () {
        return fromNode;
    }

    @Override
    public N getToNode () {
        return toNode;
    }

}

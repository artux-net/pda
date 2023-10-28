package net.artux.engine.pathfinding;

public class FlatTiledConnection extends NodeConnection<FlatTiledNode> {

    static final float NON_DIAGONAL_COST = (float)Math.sqrt(2);

    FlatTiledGraph worldMap;

    public FlatTiledConnection (FlatTiledGraph worldMap, FlatTiledNode fromNode, FlatTiledNode toNode, float cost) {
        super(fromNode, toNode, cost);
        this.worldMap = worldMap;
    }

    @Override
    public float getCost () {
        if (FlatTiledGraph.diagonal) return 1*cost;
        return cost * (!getToNode().equals(worldMap.startNode) ? NON_DIAGONAL_COST : 1);
    }
}


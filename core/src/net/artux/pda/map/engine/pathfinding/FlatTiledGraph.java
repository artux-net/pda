package net.artux.pda.map.engine.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.engine.data.GlobalData;

public class FlatTiledGraph implements TiledGraph<FlatTiledNode> {
    public static final int tileSize = 8;

    public int sizeX;
    public int sizeY;

    protected Array<FlatTiledNode> nodes;

    public static boolean diagonal;
    public FlatTiledNode startNode;

    public FlatTiledGraph(MapBorders mapBorders) {
        this.sizeX = GlobalData.mapWidth / tileSize;
        this.sizeY = GlobalData.mapHeight / tileSize;
        this.nodes = new Array<>(sizeX * sizeY);
        diagonal = true;

        mapBorders.setTilesSize(sizeX, sizeY);

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                nodes.add(new FlatTiledNode(this, x, y, x * tileSize + tileSize / 2, y * tileSize + tileSize / 2, mapBorders.getTileTypeInTileForMob(x, y), 4));
            }
        }

        for (int x = 0; x < sizeX; x++) {
            int idx = x * sizeY;
            for (int y = 0; y < sizeY; y++) {
                FlatTiledNode n = nodes.get(idx + y);
                /*if (x > 0 && y>0)
                    addConnection(n, -1, -1);
                if (x > 0 && y < sizeY - 1)
                    addConnection(n, -1, +1);
                if (x < sizeX - 1 && y < sizeY - 1)
                    addConnection(n, +1, +1);
                if (x < sizeX - 1 && y>0)
                    addConnection(n, +1, -1);*/
                if (x > 0) addConnection(n, -1, 0);
                if (y > 0) addConnection(n, 0, -1);
                if (x < sizeX - 1) addConnection(n, 1, 0);
                if (y < sizeY - 1) addConnection(n, 0, 1);
            }
        }

    }


    public FlatTiledNode getNodeInPosition(float x, float y) {
        int xTile = (int) (x / tileSize);
        int yTile = (int) (y / tileSize);

        if (xTile >= sizeX)
            xTile = sizeX - 1;
        if (yTile >= sizeY)
            yTile = sizeY - 1;

        return getNode(xTile, yTile);
    }

    public int getTypeInPosition(float x, float y) {
        int xTile = (int) (x / tileSize);
        int yTile = (int) (y / tileSize);

        if (x * sizeY + y >= nodes.size) {
            return TiledNode.TILE_WALL;
        } else return getNode(xTile, yTile).type;
    }

    public FlatTiledNode getNodeInPosition(Vector2 vector2) {
        return getNodeInPosition(vector2.x, vector2.y);
    }

    @Override
    public FlatTiledNode getNode(int x, int y) {
        return nodes.get(x * sizeY + y);
    }

    @Override
    public FlatTiledNode getNode(int index) {
        return nodes.get(index);
    }

    @Override
    public int getIndex(FlatTiledNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<FlatTiledNode>> getConnections(FlatTiledNode fromNode) {
        return fromNode.getConnections();
    }

    private void addConnection(FlatTiledNode n, int xOffset, int yOffset) {
        FlatTiledNode target = getNode(n.x + xOffset, n.y + yOffset);
        if (target.type != FlatTiledNode.TILE_WALL)
            n.getConnections().add(new FlatTiledConnection(this, n, target, target.type));
    }

    public void dispose() {
        for (FlatTiledNode node :
                nodes) {
            node.graph = null;
            for (Connection<FlatTiledNode> c : node.connections) {
                FlatTiledConnection flatTiledConnection = (FlatTiledConnection) c;
                flatTiledConnection.worldMap = null;
                flatTiledConnection.fromNode = null;
                flatTiledConnection.toNode = null;
            }
            node.getConnections().clear();
        }
        nodes.clear();
        nodes = null;
    }
}
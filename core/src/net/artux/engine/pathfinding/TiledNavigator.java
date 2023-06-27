package net.artux.engine.pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import net.artux.pda.map.engine.entities.ai.TileType;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class TiledNavigator {

    private final TiledMapTileLayer layer;

    @Inject
    public TiledNavigator(TiledMap tiledMap) {
        layer = (TiledMapTileLayer) tiledMap.getLayers().get("tiles");
    }

    public TileType getTileType(float x, float y) {
        if (x < 0 && x > layer.getWidth() && y < 0 && y > layer.getHeight())
            return TileType.EMPTY;
        int tiledX = (int) (x / layer.getTileWidth());
        int tiledY = (int) (y / layer.getTileHeight());
        return getTileTypeInTileForMob(tiledX, tiledY);
    }

    public TileType getTileTypeInTileForMob(int tiledX, int tiledY) {
        TiledMapTileLayer.Cell cell = layer.getCell(tiledX, tiledY);
        if (cell == null)
            return TileType.EMPTY;
        int id = cell.getTile().getId();
        return TileType.get(id);
    }

    public TiledMapTileLayer getLayer() {
        return layer;
    }

    public float getMapHeight() {
        return layer.getTileHeight() * layer.getHeight();
    }

    public float getMapWidth() {
        return layer.getTileWidth() * layer.getWidth();
    }
}

package net.artux.pda.map.engine.pathfinding;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class MapBorders {

    private Pixmap mobPixmap; //TODO dispose
    private Texture mobLayout;

    private Pixmap playerPixmap; //TODO dispose
    private Texture playerLayout;

    int tileWidth;
    int tileHeight;

    public MapBorders(Texture mobLayout, Texture playerBounds) {
        this.mobLayout = mobLayout;
        if (mobLayout != null) {
            if (!mobLayout.getTextureData().isPrepared()) {
                mobLayout.getTextureData().prepare();
            }
            mobPixmap = mobLayout.getTextureData().consumePixmap();
        }
        if (playerBounds!=null) {
            this.playerLayout = playerBounds;
            if (!playerBounds.getTextureData().isPrepared()) {
                playerBounds.getTextureData().prepare();
            }
            playerPixmap = playerBounds.getTextureData().consumePixmap();
        }
    }

    public boolean isMobTilesActive(){
        return mobLayout != null;
    }

    public int getWidth() {
        return mobLayout.getWidth();
    }

    public int getHeight() {
        return mobLayout.getHeight();
    }

    void setTilesSize(int width, int height){
        this.tileWidth = mobLayout.getWidth()/width;
        this.tileHeight = mobLayout.getHeight()/height;
    }

    public int getTileType(int x, int y){
        Color color = new Color(playerPixmap.getPixel(x, playerLayout.getHeight() - y));
        if (color.r == 1 && color.b == 1 && color.g == 1)
            return TiledNode.TILE_EMPTY;
        if (color.r == 1)
            return TiledNode.TILE_WALL;
        if (color.g == 1 && color.b == 1)
            return TiledNode.TILE_SWAMP;
        if (color.b == 1)
            return TiledNode.TILE_ROAD;
        if (color.g == 1)
            return TiledNode.TILE_GRASS;
        return TiledNode.TILE_EMPTY;
    }

    public int getTileTypeInTileForMob(int xTile, int yTile){
        if (mobLayout != null) {
            int x = xTile * tileWidth;
            int y = yTile * tileHeight;

            Color color = new Color(mobPixmap.getPixel(x, mobLayout.getHeight() - y));
            if (color.r == 1 && color.b == 1 && color.g == 1)
                return TiledNode.TILE_EMPTY;
            if (color.r == 1)
                return TiledNode.TILE_WALL;
            if (color.g == 1 && color.b == 1)
                return TiledNode.TILE_SWAMP;
            if (color.b == 1)
                return TiledNode.TILE_ROAD;
            if (color.g == 1)
                return TiledNode.TILE_GRASS;
        }
        return TiledNode.TILE_EMPTY;
    }

}

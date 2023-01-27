package net.artux.pda.map.engine.pathfinding;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import net.artux.engine.resource.types.NetFile;
import net.artux.pda.model.map.GameMap;

public class MapBorder implements Disposable {

    private final Texture playerLayout;
    private final Texture mobLayout;
    private Pixmap playerPixmap;
    private Pixmap mobPixmap;

    private int tileWidth;
    private int tileHeight;

    public MapBorder(AssetManager finder, GameMap map) {
        mobLayout = (Texture) finder.get(map.getTilesTexture(), NetFile.class).file;//todo get from asset manager
        playerLayout = (Texture) finder.get(map.getBoundsTexture(), NetFile.class).file;

        if (mobLayout != null) {
            prepareTexture(mobLayout);
            mobPixmap = mobLayout.getTextureData().consumePixmap();
        }
        if (playerLayout != null) {
            prepareTexture(playerLayout);
            playerPixmap = playerLayout.getTextureData().consumePixmap();
        }
    }

    private void prepareTexture(Texture texture) {
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
    }

    public boolean isMobTilesActive() {
        return mobLayout != null;
    }

    public int getMapWidth() {
        return mobLayout.getWidth();
    }

    public int getMapHeight() {
        return mobLayout.getHeight();
    }

    void setTilesSize(int width, int height) {
        this.tileWidth = mobLayout.getWidth() / width;
        this.tileHeight = mobLayout.getHeight() / height;
    }

    public float getK(float x, float y) {
        int value = playerPixmap.getPixel((int) x, playerLayout.getHeight() - (int) y);

        float r = ((value & 0xff000000) >>> 24) / 255f;
        float g = ((value & 0x00ff0000) >>> 16) / 255f;
        float b = ((value & 0x0000ff00) >>> 8) / 255f;
        float a = ((value & 0x000000ff)) / 255f;

        if (r == 1 && b == 1 && g == 1)
            return 1;
        if (a > 0.99f)
            a = 1;
        return 1 - r * a;
    }

    public int getTileType(float x, float y) {
        int value = mobPixmap.getPixel((int) x, mobLayout.getHeight() - (int) y);

        float r = ((value & 0xff000000) >>> 24) / 255f;
        float g = ((value & 0x00ff0000) >>> 16) / 255f;
        float b = ((value & 0x0000ff00) >>> 8) / 255f;

        if (r == 1 && b == 1 && g == 1)
            return TiledNode.TILE_EMPTY;
        if (r == 1)
            return TiledNode.TILE_WALL;
        if (g == 1 && b == 1)
            return TiledNode.TILE_SWAMP;
        if (b == 1)
            return TiledNode.TILE_ROAD;
        if (g == 1)
            return TiledNode.TILE_GRASS;
        return TiledNode.TILE_EMPTY;
    }

    public int getTileTypeInTileForMob(int xTile, int yTile) {
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

    public Texture getPlayerLayout() {
        return playerLayout;
    }

    @Override
    public void dispose() {
        if (mobPixmap != null)
            mobPixmap.dispose();
        if (playerPixmap != null)
            playerPixmap.dispose();

    }
}

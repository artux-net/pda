package net.artux.pda.map.utils;

public class MapInfo {
    private final int mapWidth;
    private final int mapHeight;

    public MapInfo(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }
}

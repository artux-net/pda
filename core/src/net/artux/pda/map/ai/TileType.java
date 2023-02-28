package net.artux.pda.map.ai;

import java.util.HashMap;

public enum TileType {

    EMPTY(0, Integer.MAX_VALUE, false),
    ROAD(1, 1, true),
    GROUND(2, 2, true),
    WALL(3, Integer.MAX_VALUE, false),
    GRASS(6, 2, true),
    ANOMALY(4, GRASS.weight, true),
    GLASS(5, 2, true),
    SWAMP(8, 4, true),
    WATER(9, 3, true),
    SEARCH(10, GRASS.weight, true),
    WEAK_RADIATION(7, GRASS.weight, true),
    MIDDLE_RADIATION(11, GRASS.weight, true),
    STRONG_RADIATION(12, GRASS.weight, true);

    private final int id;
    private final int weight;
    private final boolean walkable;

    TileType(int id, int weight, boolean walkable) {
        this.id = id;
        this.weight = weight;
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public int getWeight() {
        return weight;
    }

    private static final HashMap<Integer, TileType> types = new HashMap<>(TileType.values().length);

    static {
        for (TileType t :
                TileType.values()) {
            types.put(t.id, t);
        }
    }

    public static TileType get(Integer id) {
        return types.getOrDefault(id, EMPTY);
    }
}

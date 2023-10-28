package net.artux.pda.map.ecs.physics;

import com.badlogic.gdx.maps.MapProperties;

public enum WorldObject {
    GLASS(true),
    WALL(false);

    private final boolean fire;

    WorldObject(boolean fire) {
        this.fire = fire;
    }

    public static WorldObject detectType(MapProperties mapProperties){
        Boolean fire = mapProperties.get("fire", Boolean.class);
        if (fire != null && fire)
            return GLASS;

        return WALL;
    }
}
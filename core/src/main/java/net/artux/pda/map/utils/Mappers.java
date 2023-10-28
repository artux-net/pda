package net.artux.pda.map.utils;

import com.badlogic.gdx.math.Vector2;

public final class Mappers {

    public static Vector2 vector2(String pos) {
        if (pos != null) {
            String[] s = pos.split(":");
            return new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }
        return new Vector2();
    }

}

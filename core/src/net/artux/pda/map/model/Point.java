package net.artux.pda.map.model;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class Point {
    public int type;
    private String pos;
    private Vector2 position = null;
    private HashMap<String, String> data;

    public HashMap<String, String> getData() {
        return data;
    }

    public Vector2 getPosition() {
        if (position==null) {
            String[] s = pos.split(":");
            position = new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }
        return position;
    }

}

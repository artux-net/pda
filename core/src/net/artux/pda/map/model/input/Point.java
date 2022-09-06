package net.artux.pda.map.model.input;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Point implements Serializable {
    public int type;
    private String name;
    private String pos;
    private Vector2 position = null;
    private HashMap<String, String> data;
    public HashMap<String, List<String>> condition;

    public HashMap<String, String> getData() {
        return data;
    }

    public HashMap<String, List<String>> getCondition() {
        return condition;
    }

    public String getTitle() {
        return name;
    }

    public Vector2 getPosition() {
        if (position==null) {
            String[] s = pos.split(":");
            position = new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }
        return position;
    }

}

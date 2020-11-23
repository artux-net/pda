package net.artux.pda.map.model;

import com.badlogic.gdx.math.Vector2;

public class Transfer {
    public String pos;
    private String toPos;
    private String message;
    private int to;

    public Vector2 getPosition() {
        if (pos!=null) {
            String[] s = pos.split(":");
            return new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }
        return null;
    }

    public String getToPosition() {
        return toPos;
    }

    public String getMessage() {
        return message;
    }

    public int getTo() {
        return to;
    }
}

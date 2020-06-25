package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Map {

    private int id;
    private String title;
    private String texture;
    private String boundsTexture;
    private String defPos;
    private List<Point> points;
    private List<Spawn> spawns;

    public String getTitle() {
        return title;
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Spawn> getSpawns() {
        return spawns;
    }

    public void setPlayerPos(String pos) {
        this.defPos = pos;
    }

    public Vector2 getPlayerPosition() {
        String[] s = defPos.split(":");
        return new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
    }

    public Texture getTexture() {
        return new Texture(texture);
    }

    public Texture getBoundsTexture() {
        return new Texture(boundsTexture);
    }
}

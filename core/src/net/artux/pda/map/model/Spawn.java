package net.artux.pda.map.model;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.HashMap;


public class Spawn implements Serializable {

    private int id;
    private int r;
    private int n;
    private String pos;
    private boolean angry;
    private boolean ignorePlayer;
    private HashMap<String, String> data;

    public HashMap<String, String> getData() {
        return data;
    }

    public Vector2 getPosition() {
        String[] s = pos.split(":");
        return new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
    }

    public boolean isAngry() {
        return angry;
    }

    public boolean isIgnorePlayer() {
        return ignorePlayer;
    }

    public int getN() {
        return n;
    }

    public int getR() {
        return r;
    }

    public int getId() {
        return id;
    }

    public String getPos() {
        return pos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
    }

    public void setIgnorePlayer(boolean ignorePlayer) {
        this.ignorePlayer = ignorePlayer;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
}

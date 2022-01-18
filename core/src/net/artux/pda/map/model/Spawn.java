package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PlayState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Spawn {

    private int id;
    private int r;
    private int n;
    private String pos;
    private boolean angry;
    private boolean ignorePlayer;
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

    public Vector2 getPos() {
        return position;
    }

    public int getId() {
        return id;
    }
}

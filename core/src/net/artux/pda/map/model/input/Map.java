package net.artux.pda.map.model.input;

import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.model.Spawn;
import net.artux.pda.map.model.Transfer;

import java.util.List;

public class Map {

    private int id;
    private String title;
    private String texture;
    private String tilesTexture;
    private String boundsTexture;
    private String blurTexture;
    private String defPos;
    private List<Point> points;
    private List<Transfer> transfers;
    private List<Spawn> spawns;

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setBoundsTexture(String boundsTexture) {
        this.boundsTexture = boundsTexture;
    }

    public void setBlurTexture(String blurTexture) {
        this.blurTexture = blurTexture;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
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

    public String getTilesTexture() {
        return tilesTexture;
    }

    public Vector2 getPlayerPosition() {
        if (defPos!=null) {
            String[] s = defPos.split(":");
            return new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }else
            return new Vector2(500,500);
    }

    public String getTextureUri() {
        return texture;
    }

    public String getBoundsTextureUri() {
        return boundsTexture;
    }

    public String getBlurTextureUri() {
        return blurTexture;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }
}

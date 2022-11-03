package net.artux.pda.model.map;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class GameMap implements Serializable {

    private int id;
    private String title;
    private String texture;
    private String tilesTexture;
    private String boundsTexture;
    private String blurTexture;
    private String defPos;
    private List<Point> points;
    private List<SpawnModel> spawns;

}

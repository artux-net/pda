package net.artux.pda.model.map;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import lombok.Data;

@Data
public class GameMap implements Serializable {

    private int id;
    private String title;
    private String tmx;
    private String texture;
    private String tilesTexture;
    private String boundsTexture;
    private String defPos;
    private List<Point> points;
    private List<SpawnModel> spawns;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMap map = (GameMap) o;
        return id == map.id && Objects.equals(title, map.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}

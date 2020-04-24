package net.artux.pda.map.model;

import java.util.List;

public class Map {

    private String title;
    private String src;
    private String boundsSrc;
    private Position playerPosition;
    private List<Point> points;
    private List<Bot> bots;

    public Map(String title, String src, String boundsSrc, Position playerPosition, List<Point> points) {
        this.title = title;
        this.src = src;
        this.boundsSrc = boundsSrc;
        this.playerPosition = playerPosition;
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public String getSrc() {
        return src;
    }

    public String getBoundsSrc() {
        return boundsSrc;
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Bot> getBots() {
        return bots;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }
}

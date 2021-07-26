package net.artux.pdalib.arena;

public class Position {

    public float x;
    public float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void moveBy(double x, double y){
        this.x += x;
        this.y += y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

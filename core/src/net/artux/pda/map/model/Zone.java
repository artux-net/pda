package net.artux.pda.map.model;

public class Zone {

    private final float leftX;
    private final float rightX;
    private final float bottomY;
    private final float topY;

    public Zone(float leftX, float rightX, float bottomY, float topY) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.bottomY = bottomY;
        this.topY = topY;
    }

    public float getLeftX() {
        return leftX;
    }

    public float getRightX() {
        return rightX;
    }

    public float getBottomY() {
        return bottomY;
    }

    public float getTopY() {
        return topY;
    }
}

package net.artux.pda.map.models.items;

public class Weapon extends Wearable {

    private float precision;
    private float speed;
    private float damage;
    private float condition;
    private int bulletQuantity;
    private int bulletId;

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getCondition() {
        return condition;
    }

    public void setCondition(float condition) {
        this.condition = condition;
    }

    public int getBulletQuantity() {
        return bulletQuantity;
    }

    public void setBulletQuantity(int bulletQuantity) {
        this.bulletQuantity = bulletQuantity;
    }

    public int getBulletId() {
        return bulletId;
    }

    public void setBulletId(int bulletId) {
        this.bulletId = bulletId;
    }
}

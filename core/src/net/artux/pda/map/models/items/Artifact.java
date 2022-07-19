package net.artux.pda.map.models.items;

public class Artifact extends Wearable {

    private int anomalyId;
    private int health;
    private int radio;
    private int damage;
    private int bleeding;
    private int thermal;
    private int chemical;
    private int endurance;
    private int electric;

    public int getAnomalyId() {
        return anomalyId;
    }

    public void setAnomalyId(int anomalyId) {
        this.anomalyId = anomalyId;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getBleeding() {
        return bleeding;
    }

    public void setBleeding(int bleeding) {
        this.bleeding = bleeding;
    }

    public int getThermal() {
        return thermal;
    }

    public void setThermal(int thermal) {
        this.thermal = thermal;
    }

    public int getChemical() {
        return chemical;
    }

    public void setChemical(int chemical) {
        this.chemical = chemical;
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    public int getElectric() {
        return electric;
    }

    public void setElectric(int electric) {
        this.electric = electric;
    }
}

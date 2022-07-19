package net.artux.pda.map.models.items;

public class Armor extends Wearable {

    private float thermal_pr;
    private float electric_pr;
    private float chemical_pr;
    private float radio_pr;
    private float psy_pr;
    private float damage_pr;
    private float condition;

    public float getThermal_pr() {
        return thermal_pr;
    }

    public void setThermal_pr(float thermal_pr) {
        this.thermal_pr = thermal_pr;
    }

    public float getElectric_pr() {
        return electric_pr;
    }

    public void setElectric_pr(float electric_pr) {
        this.electric_pr = electric_pr;
    }

    public float getChemical_pr() {
        return chemical_pr;
    }

    public void setChemical_pr(float chemical_pr) {
        this.chemical_pr = chemical_pr;
    }

    public float getRadio_pr() {
        return radio_pr;
    }

    public void setRadio_pr(float radio_pr) {
        this.radio_pr = radio_pr;
    }

    public float getPsy_pr() {
        return psy_pr;
    }

    public void setPsy_pr(float psy_pr) {
        this.psy_pr = psy_pr;
    }

    public float getDamage_pr() {
        return damage_pr;
    }

    public void setDamage_pr(float damage_pr) {
        this.damage_pr = damage_pr;
    }

    public float getCondition() {
        return condition;
    }

    public void setCondition(float condition) {
        this.condition = condition;
    }
}

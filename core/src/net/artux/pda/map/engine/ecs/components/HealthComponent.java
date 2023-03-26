package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.MedicineModel;

public class HealthComponent implements Component {

    private boolean immortal = false;

    private Float health;
    private Float stamina;
    private Float radiation;

    public HealthComponent() {
        health = 100f;
        stamina = 100f;
        radiation = 0f;
    }

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
    }

    public float getHealth() {
        return health;
    }

    public float getStamina() {
        return stamina;
    }

    public float getRadiation() {
        return radiation;
    }

    public boolean isDead() {
        return health < 1;
    }

    public void setHealth(Float health) {
        this.health = health;
    }

    public void setRadiation(Float radiation) {
        this.radiation = radiation;
    }

    public void damage(float damage) {
        health(-damage);
    }

    public void health(float value) {
        if (immortal)
            return;

        float result = health + value;
        if (result > 0)
            if (result > 100)
                health = 100f;
            else
                health = result;
        else health = 0f;
    }

    public void stamina(float value) {
        float result = stamina + value;
        if (result > 0)
            if (result > 100)
                stamina = 100f;
            else
                stamina = result;
        else stamina = 0f;
    }

    public void radiation(float value) {
        float result = radiation + value;
        if (result > 0)
            if (result > 100)
                radiation = 100f;
            else
                radiation = result;
        else radiation = 0f;
    }

    public void treat(MedicineModel model) {
        health(model.getHealth());
        stamina += model.getStamina();
        radiation(model.getRadiation());
    }
}
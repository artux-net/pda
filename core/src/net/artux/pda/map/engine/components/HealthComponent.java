package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.MedicineModel;

public class HealthComponent implements Component {

    public float value;
    public float stamina;
    public float radiation;

    public HealthComponent() {
        value = 100;
        stamina = 100;
    }

    public boolean isDead() {
        return value < 1;
    }

    public void damage(float damage) {
        value -= damage;
    }

    public void treat(float med) {
        if (value + med < 100)
            value += med;
        else
            value = 100;
    }

    public void decreaseRadiation(float value) {
        if (value < 0)
            value = -value;
        if (radiation - value > 0)
            radiation -= value;
        else radiation = 0;
    }

    public void treat(MedicineModel model) {
        treat(model.getHealth());
        stamina += model.getStamina();
        decreaseRadiation(model.getRadiation());
    }
}
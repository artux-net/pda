package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.MedicineModel;

public class HealthComponent implements Component {

    public float value;
    public float stamina;
    public float radiation;

    public HealthComponent() {
        value = 100;
    }

    public boolean isDead(){
        return value<1;
    }

    public void damage(float damage){
        value -= damage;
    }
    public void treat(float med){
        if (value  + med < 100)
            value += med;
        else
            value = 100;
    }

    public void decreaseRadiation(float value){
        if (radiation - value > 0)
            radiation -= value;
        else radiation = 0;
    }

    public void applyMedicine(MedicineModel model){
        radiation += model.getRadiation();
        stamina += model.getStamina();
        value += model.getHealth();
    }

}
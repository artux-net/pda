package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {

    public float value;
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

}
package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {

    public float value;

    public HealthComponent() {
        value = 100;
    }

    public boolean isDead(){
        return value<1;
    }

}
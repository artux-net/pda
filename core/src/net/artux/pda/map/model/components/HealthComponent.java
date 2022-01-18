package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pdalib.Member;

public class HealthComponent implements Component {

    public float value;

    public HealthComponent() {
        value = 100;
    }



    public boolean isDead(){
        return value<0;
    }

}
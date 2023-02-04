package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;

public class AnomalyComponent implements Component {

    private String name;
    public int size;
    public float maxVelocity;
    public float damage;

    public AnomalyComponent(String name, int size, float maxVelocity, float damage) {
        this.name = name;
        this.size = size;
        this.maxVelocity = maxVelocity;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public String desc(){
        return "Объект: " +
                "Аномалия" +
                '\n' +
                "Наименование: " +
                name +
                '\n' +
                "Размер: " +
                size * 2;
    }

}
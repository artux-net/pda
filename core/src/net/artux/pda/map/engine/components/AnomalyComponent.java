package net.artux.pda.map.engine.components;

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Объект: ");
        stringBuilder.append("Аномалия");
        stringBuilder.append('\n');
        stringBuilder.append("Наименование: ");
        stringBuilder.append(name);
        stringBuilder.append('\n');
        stringBuilder.append("Размер: ");
        stringBuilder.append(size * 2);
        return stringBuilder.toString();
    }

}
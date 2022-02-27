package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.map.model.MobType;

import java.util.List;

public class ControlPointComponent implements Component {

    public String title;
    public MobType mobType;
    public List<Entity> entityList;

    public ControlPointComponent(String title, MobType mobType, List<Entity> entityList) {
        this.title = title;
        this.entityList = entityList;
        this.mobType = mobType;
    }

    public String desc() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Объект: ");
        stringBuilder.append(title);
        stringBuilder.append('\n');
        stringBuilder.append("Занят: ");
        stringBuilder.append(mobType.name);
        stringBuilder.append('\n');
        stringBuilder.append("Количество: ");
        stringBuilder.append(entityList.size());
        return stringBuilder.toString();
    }
}
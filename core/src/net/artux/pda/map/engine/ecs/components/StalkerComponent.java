package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.ItemModel;

import java.util.List;

public class StalkerComponent implements Component {

    private String name;
    public String group;
    public String avatar;
    private List<ItemModel> inventory;

    public StalkerComponent(String name, String avatar, List<ItemModel> inventory) {
        this.name = name;
        this.avatar = avatar;
        this.inventory = inventory;
    }

    public List<ItemModel> getInventory() {
        return inventory;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

}
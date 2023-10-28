package net.artux.pda.map.ecs.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.StringBuilder;

import net.artux.pda.model.items.ItemModel;

import java.util.List;

public class EntityComponent implements Component {

    private String description;
    private final String name;
    public String group;
    public String avatar;
    private final List<ItemModel> inventory;

    public EntityComponent(String name, String avatar, List<ItemModel> inventory) {
        this.name = name;
        this.avatar = avatar;
        this.inventory = inventory;
    }

    public EntityComponent(String description, String name, String avatar, List<ItemModel> inventory) {
        this.description = description;
        this.name = name;
        this.avatar = avatar;
        this.inventory = inventory;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    private StringBuilder builder;

    public StringBuilder getDescription() {
        if (builder == null) {
            builder = new StringBuilder();
            if (description != null) {
                builder.append("[").append(description).append("]");
                builder.append(' ');
            }
            builder.append(name);
        }
        return builder;
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
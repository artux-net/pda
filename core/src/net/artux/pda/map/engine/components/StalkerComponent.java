package net.artux.pda.map.engine.components;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.items.ItemModel;

import java.util.List;

public class StalkerComponent implements Component {

    private String name;
    public String group;
    public int avatar;
    private List<ItemModel> inventory;

    public StalkerComponent(String name, List<ItemModel> inventory) {
        this.name = name;
        this.inventory = inventory;
        avatar = random(0, 30);
    }

    public String getName() {
        return name;
    }

}
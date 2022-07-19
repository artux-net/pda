package net.artux.pda.map.models.user;

import net.artux.pda.map.models.items.Armor;
import net.artux.pda.map.models.items.Artifact;
import net.artux.pda.map.models.items.Item;
import net.artux.pda.map.models.items.ItemType;
import net.artux.pda.map.models.items.Weapon;
import net.artux.pda.map.models.items.Wearable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GdxData {

    private List<ParameterGdx> parameters;
    private List<StoryStateGdx> storyStates;

    private List<Armor> armors = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();
    private List<Artifact> artifacts = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    public List<StoryStateGdx> getStoryStates() {
        return storyStates;
    }

    public boolean containsCurrent() {
        return getCurrent() != null;
    }

    public StoryStateGdx getCurrent() {
        if (storyStates != null)
            for (StoryStateGdx state : storyStates) {
                if (state.isCurrent())
                    return state;
            }
        return null;
    }

    public List<ParameterGdx> getParameters() {
        return parameters;
    }

    public HashMap<String, Integer> getParametersMap() {
        HashMap<String, Integer> map = new HashMap<>();
        for (ParameterGdx param : parameters) {
            map.put(param.getKey(), param.getValue());
        }
        return map;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        items.addAll(weapons);
        items.addAll(armors);
        items.addAll(artifacts);
        items.addAll(this.items);
        return items;
    }

    public Item getCurrentWearable(ItemType type) {
        for (Item item : getAllItems()) {
            if (item instanceof Wearable)
                if (item.getType() == type && ((Wearable) item).isEquipped()) {
                    return item;
                }
        }
        return null;
    }

    public Item getItemByBaseId(int baseId) {
        for (Item item : getAllItems()) {
            if (item.getBaseId() == baseId) {
                return item;
            }
        }
        return null;
    }

    public void setParameters(List<ParameterGdx> parameters) {
        this.parameters = parameters;
    }

    public void setStoryStates(List<StoryStateGdx> storyStates) {
        this.storyStates = storyStates;
    }

    public List<Armor> getArmors() {
        return armors;
    }

    public void setArmors(List<Armor> armors) {
        this.armors = armors;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}

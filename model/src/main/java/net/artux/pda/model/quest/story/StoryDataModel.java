package net.artux.pda.model.quest.story;

import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ArtifactModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WeaponModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryDataModel {

    private List<ParameterModel> parameters;
    private List<StoryStateModel> storyStates;

    private List<ArmorModel> armors = new ArrayList<>();
    private List<WeaponModel> weapons = new ArrayList<>();
    private List<ArtifactModel> artifacts = new ArrayList<>();
    private List<ItemModel> items = new ArrayList<>();

    public List<StoryStateModel> getStoryStates() {
        return storyStates;
    }

    public boolean containsCurrent() {
        return getCurrent() != null;
    }

    public StoryStateModel getCurrent() {
        if (storyStates != null)
            for (StoryStateModel state : storyStates) {
                if (state.isCurrent())
                    return state;
            }
        return null;
    }

    public HashMap<String, Integer> getParametersMap() {
        HashMap<String, Integer> map = new HashMap<>();
        for (ParameterModel param : parameters) {
            map.put(param.getKey(), param.getValue());
        }
        return map;
    }

    public List<ItemModel> getAllItems() {
        List<ItemModel> items = new ArrayList<>();
        items.addAll(weapons);
        items.addAll(armors);
        items.addAll(artifacts);
        items.addAll(this.items);
        return items;
    }

    public void setParameters(List<ParameterModel> parameters) {
        this.parameters = parameters;
    }

    public void setStoryStates(List<StoryStateModel> storyStates) {
        this.storyStates = storyStates;
    }

    public List<ParameterModel> getParameters() {
        return parameters;
    }

    public List<ArmorModel> getArmors() {
        return armors;
    }

    public void setArmors(List<ArmorModel> armors) {
        this.armors = armors;
    }

    public List<WeaponModel> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<WeaponModel> weapons) {
        this.weapons = weapons;
    }

    public List<ArtifactModel> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<ArtifactModel> artifacts) {
        this.artifacts = artifacts;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }
}

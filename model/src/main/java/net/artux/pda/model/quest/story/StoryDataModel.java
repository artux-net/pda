package net.artux.pda.model.quest.story;

import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ArtifactModel;
import net.artux.pda.model.items.DetectorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class StoryDataModel {

    private List<ParameterModel> parameters;
    private List<StoryStateModel> storyStates;

    private List<ArmorModel> armors;
    private List<WeaponModel> weapons;
    private List<MedicineModel> medicines;
    private List<DetectorModel> detectors;
    private List<ArtifactModel> artifacts;
    private List<ItemModel> bullets;
    private int money;


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

    public ItemModel getCurrentWearable(ItemType type) {
        for (ItemModel item : getAllItems()) {
            if (item instanceof WearableModel)
                if (item.getType() == type && ((WearableModel) item).isEquipped()) {
                    return item;
                }
        }
        return null;
    }

    public ItemModel getItemByBaseId(int baseId) {
        for (ItemModel item : getAllItems()) {
            if (item.getBaseId() == baseId) {
                return item;
            }
        }
        return null;
    }

    public List<ItemModel> getAllItems() {
        List<ItemModel> items = new LinkedList<>();
        items.addAll(weapons);
        items.addAll(armors);
        items.addAll(artifacts);
        items.addAll(medicines);
        items.addAll(detectors);
        items.addAll(this.bullets);

        return items;
    }

    public float getTotalWeight() {
        float weight = 0;
        for (ItemModel item : getAllItems())
            weight += item.getWeight() * item.getQuantity();
        return weight;
    }

}

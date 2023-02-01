package net.artux.pda.model.quest.story;

import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ArtifactModel;
import net.artux.pda.model.items.DetectorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;
import net.artux.pda.model.user.Gang;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class StoryDataModel implements Serializable {

    private String name;
    private String nickname;
    private String login;
    private String avatar;
    private int money;
    private int xp;
    private int pdaId;
    private Gang gang;

    private List<ParameterModel> parameters;
    private List<StoryStateModel> storyStates;

    private List<ArmorModel> armors;
    private List<WeaponModel> weapons;
    private List<MedicineModel> medicines;
    private List<DetectorModel> detectors;
    private List<ArtifactModel> artifacts;
    private List<ItemModel> bullets;

    public boolean containsCurrent() {
        return getCurrentState() != null;
    }

    public StoryStateModel getCurrentState() {
        if (storyStates != null)
            for (StoryStateModel state : storyStates) {
                if (state.isCurrent())
                    return state;
            }
        return null;
    }

    public StoryStateModel getStateByStoryId(int id) {
        if (storyStates != null)
            for (StoryStateModel state : storyStates) {
                if (state.getStoryId() == id)
                    return state;
            }
        return null;
    }

    public HashMap<String, Integer> getParametersMap() {
        HashMap<String, Integer> map = new HashMap<>();
        if (parameters != null && parameters.size() > 0)
            for (ParameterModel param : parameters) {
                map.put(param.getKey(), param.getValue());
            }
        return map;
    }

    public WearableModel getEquippedWearable(ItemType type) {
        for (ItemModel item : getAllItems()) {
            if (item instanceof WearableModel)
                if (item.getType() == type)
                    if (((WearableModel) item).isEquipped()) {
                        return (WearableModel) item;
                    }
        }
        return null;
    }

    public void setCurrentWearable(WearableModel itemModel) {
        WearableModel current = getEquippedWearable(itemModel.getType());
        if (current != null)
            current.setEquipped(false);
        if (current != itemModel)
            itemModel.setEquipped(true);
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

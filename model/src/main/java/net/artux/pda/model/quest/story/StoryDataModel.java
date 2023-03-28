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
import net.artux.pda.model.user.GangRelation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private GangRelation relations;

    private List<ParameterModel> parameters;
    private List<StoryStateModel> storyStates;

    private List<ArmorModel> armors;
    private List<WeaponModel> weapons;
    private List<MedicineModel> medicines;
    private List<DetectorModel> detectors;
    private List<ArtifactModel> artifacts;
    private List<ItemModel> bullets;

    public String getAvatar() {
        if (avatar.contains("http"))
            return avatar;
        else return "textures/avatars/a" + avatar + ".png";
    }

    public boolean containsCurrent() {
        return getCurrentState() != null;
    }

    public <T extends ItemModel> void addItem(T item) {
        if (item.getType().isCountable()) {
            addAsCountable(item);
        } else {
            item.setQuantity(1);
            addAsNotCountable(item);
        }
    }

    private <T extends ItemModel> void addAsNotCountable(T item) {
        ItemType type = item.getType();
        if (type.isWearable()) {
            boolean userWears = getEquippedWearable(type) != null;
            ((WearableModel) item).setEquipped(!userWears);
        }
        item.setQuantity(1);
        addAsIs(item);
    }

    private <T extends ItemModel> void addAsCountable(T itemEntity) {
        Optional<? extends ItemModel> optionalItem = getAllItems()
                .stream()
                .filter(item -> item.getBaseId() == itemEntity.getBaseId())
                .findFirst();
        if (optionalItem.isPresent()) {
            ItemModel item = optionalItem.get();
            item.setQuantity(item.getQuantity() + itemEntity.getQuantity());
        } else {
            addAsIs(itemEntity);
        }
    }

    private <T extends ItemModel> void addAsIs(T itemEntity) {
        switch (itemEntity.getType()) {
            case BULLET:
                getBullets().add(itemEntity);
                break;
            case ARMOR:
                getArmors().add((ArmorModel) itemEntity);
                break;
            case PISTOL:
            case RIFLE:
                getWeapons().add((WeaponModel) itemEntity);
                break;
            case ARTIFACT:
                getArtifacts().add((ArtifactModel) itemEntity);
                break;
            case DETECTOR:
                getDetectors().add((DetectorModel) itemEntity);
                break;
            case MEDICINE:
                getMedicines().add((MedicineModel) itemEntity);
                break;
        }
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

    public Rang getRang() {
        return getRang(xp);
    }

    public static Rang getRang(int xp) {
        Rang previousRang = Rang.BEGINNER;
        for (Rang rang : Rang.values()) {
            if (rang.getXp() > xp)
                return previousRang;
            else previousRang = rang;
        }
        return Rang.FINAL;
    }

    public enum Rang {
        BEGINNER(0, 0),
        NEW(1, 1000),
        STALKER(2, 3000),
        EXPERIENCE(3, 6000),
        OLD(4, 1000),
        MASTER(5, 16000),
        FINAL(6, Integer.MAX_VALUE, true);

        private final int id;
        private final int xp;
        private final boolean last;

        Rang(int id, int xp) {
            this(id, xp, false);
        }

        Rang(int id, int xp, boolean last) {
            this.id = id;
            this.xp = xp;
            this.last = last;
        }

        public int getId() {
            return id;
        }

        public boolean isLast() {
            return last;
        }

        public int getXp() {
            return xp;
        }

        public Rang getNextRang() {
            if (id < Rang.values().length - 1)
                return Rang.values()[id + 1];
            else return null;
        }
    }

}

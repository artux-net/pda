package net.artux.pda.map.content;


import static com.badlogic.gdx.math.MathUtils.random;

import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import org.apache.commons.lang3.SerializationUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

@PerGameMap
public class ItemsGenerator {

    private final StoryDataModel playerDataModel;
    private final ItemsContainerModel allItems;

    @Inject
    public ItemsGenerator(DataRepository dataRepository) {
        playerDataModel = dataRepository.getInitDataModel();
        allItems = dataRepository.getItems();
    }

    public List<ItemModel> getRandomItems() {
        List<ItemModel> items = new LinkedList<>();
        if (random() > 0.3f) {
            if (random() < 0.3f) {
                items.add(getSpecificFromList(allItems.getWeapons(), 0.15f));
            }

            if (random() < 0.1f) {
                items.add(getSpecificFromList(allItems.getArmors()));
            }

            if (random() < 0.05f) {
                items.add(getSpecificFromList(allItems.getArtifacts()));
            }

            if (random() < 0.01f) {
                items.add(getSpecificFromList(allItems.getDetectors()));
            }

            for (int i = 0; i < random(2); i++) {
                if (random() > 0.6f) {
                    ItemModel bullet = getSpecificFromList(allItems.getBullets());
                    if (bullet != null) bullet.setQuantity(random(11, 120));
                    items.add(bullet);
                }
            }

            for (int i = 0; i < random(2); i++) {
                if (random() < 0.6f) {
                    ItemModel medicine = getSpecificFromList(allItems.getMedicines(), 0.4f);
                    if (medicine != null) medicine.setQuantity(random(1, 3));
                    items.add(medicine);
                }
            }

        } else {
            ItemModel bullet = getSpecificFromList(allItems.getBullets());
            if (bullet != null) bullet.setQuantity(random(1, 15));
            items.add(bullet);
        }
        items.removeIf(Objects::isNull);
        items.forEach(item -> item = SerializationUtils.clone(item));
        return items;
    }

    public ItemModel getSpecificFromList(List<? extends ItemModel> itemModels) {
        return getSpecificFromList(itemModels, 0);
    }

    public ItemModel getSpecificFromList(List<? extends ItemModel> itemModels, int part, int of) {
        return getSpecificFromList(itemModels, part, of, 0);
    }

    public ItemModel getSpecificFromList(List<? extends ItemModel> itemModels, float plusK) {
        return getSpecificFromList(itemModels, playerDataModel.getXp(), StoryDataModel.Rang.EXPERIENCE.getXp(), plusK);
    }

    public ItemModel getSpecificFromList(List<? extends ItemModel> itemModels, int part, int of, float plusK) {
        if (itemModels.size() == 0)
            return null;

        int lastIndex = itemModels.size() - 1;
        if (part < of) {
            lastIndex = (int) (((part / of) + plusK) * lastIndex);
        }
        if (random() > 0.2f) {
            lastIndex += random(1, 2);
        }
        if (lastIndex < 0)
            lastIndex = 0;
        if (lastIndex > itemModels.size() - 1)
            lastIndex = itemModels.size() - 1;
        ItemModel item = itemModels.get(random(lastIndex));
        item.setQuantity(1);
        return itemModels.get(random(lastIndex));
    }

}

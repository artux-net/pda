package net.artux.pda.map.content;


import static com.badlogic.gdx.math.MathUtils.random;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

@PerGameMap
public class ItemsGenerator {

    private final StoryDataModel dataModel;
    private final ItemsContainerModel allItems;

    @Inject
    public ItemsGenerator(DataRepository dataRepository) {
        dataModel = dataRepository.getStoryDataModel();
        allItems = dataRepository.getItems();
    }

    public List<ItemModel> getRandomItems() {
        List<ItemModel> items = new LinkedList<>();
        if (random() > 0.3f) {
            if (random() > 0.3f) {
                items.add(generateByRang(allItems.getWeapons()));
            }

            if (random() > 0.1f) {
                items.add(generateByRang(allItems.getArmors()));
            }

            if (random() > 0.05f) {
                items.add(generateByRang(allItems.getArtifacts()));
            }

            if (random() > 0.01f) {
                items.add(generateByRang(allItems.getDetectors()));
            }

            for (int i = 0; i < random(2); i++) {
                if (random() > 0.6f) {
                    ItemModel bullet = generateByRang(allItems.getBullets());
                    if (bullet != null) bullet.setQuantity(random(11, 120));
                    items.add(bullet);
                }
            }

            for (int i = 0; i < random(2); i++) {
                if (random() > 0.6f) {
                    ItemModel medicine = generateByRang(allItems.getMedicines());
                    if (medicine != null) medicine.setQuantity(random(1, 3));
                    items.add(medicine);
                }
            }

        }
        items.removeIf(Objects::isNull);
        return items;
    }

    public ItemModel generateByRang(List<? extends ItemModel> itemModels) {
        if (itemModels.size() == 0)
            return null;

        int maxXp = StoryDataModel.Rang.EXPERIENCE.getXp();
        int xp = dataModel.getXp();
        int lastIndex = itemModels.size() - 1;
        if (xp < maxXp) {
            lastIndex = (xp / maxXp) * lastIndex;
        }
        if (lastIndex < 0)
            lastIndex = 0;

        ItemModel item = itemModels.get(random(lastIndex));
        item.setQuantity(1);
        return itemModels.get(random(lastIndex));
    }

}

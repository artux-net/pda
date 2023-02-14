package net.artux.pda.model.items;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public final class ItemsHelper implements Serializable {

    public static void add(List<ItemModel> list, ItemModel model) {
        if (model instanceof WearableModel)
            ((WearableModel) model).setEquipped(false);
        if (model.getType().isCountable()) {
            Optional<ItemModel> itemModel = list.stream()
                    .filter(item -> item.baseId == model.baseId)
                    .findFirst();
            if (itemModel.isPresent()) {
                ItemModel item = itemModel.get();
                item.setQuantity(item.getQuantity() + model.getQuantity());
            } else {
                list.add(model);
            }
        } else {
            model.setQuantity(1);
            list.add(model);
        }
    }

}

package net.artux.pda.model.items

object ItemsHelper {
    fun add(list: MutableList<ItemModel>, model: ItemModel) {
        if (model is WearableModel) model.isEquipped = false
        if (model.type.isCountable) {
            val itemModel = list
                .firstOrNull { item: ItemModel -> item.baseId == model.baseId }
            if (itemModel != null) {
                itemModel.quantity += model.quantity
            } else {
                list.add(model)
            }
        } else {
            model.quantity = 1
            list.add(model)
        }
    }
}
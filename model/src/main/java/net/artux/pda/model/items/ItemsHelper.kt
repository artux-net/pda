package net.artux.pda.model.items

import java.io.Serializable

object ItemsHelper : Serializable {
    fun add(list: MutableList<ItemModel>, model: ItemModel) {
        if (model is WearableModel) model.isEquipped = false
        if (model.type.isCountable) {
            val itemModel = list.stream()
                .filter { item: ItemModel -> item.baseId == model.baseId }
                .findFirst()
            if (itemModel.isPresent) {
                val item = itemModel.get()
                item.quantity = item.quantity + model.quantity
            } else {
                list.add(model)
            }
        } else {
            model.quantity = 1
            list.add(model)
        }
    }
}
package net.artux.pda.map.view.collection.table

import net.artux.pda.model.items.ItemModel

interface OnItemClickListener {
    fun onTap(itemModel: ItemModel)
    fun onLongPress(itemModel: ItemModel)
}
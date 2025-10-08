package net.artux.pda.model.items

import java.io.Serializable
import java.util.UUID

open class ItemModel : Serializable {
    var id: UUID? = UUID.randomUUID()
    var type: ItemType = ItemType.ITEM
    var icon: String? = null
    var title: String? = null

    @JvmField
    var baseId = 0
    var weight = 0f
    var price = 0
    var quantity = 0
}
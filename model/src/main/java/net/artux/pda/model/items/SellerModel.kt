package net.artux.pda.model.items

import java.util.LinkedList

class SellerModel {
    var id: Long? = null
    var name: String? = null
    var icon: String? = null
    var image: String? = null
    var armors: List<ArmorModel>? = null
    var weapons: List<WeaponModel>? = null
    var artifacts: List<ArtifactModel>? = null
    var bullets: List<ItemModel>? = null
    var medicines: List<MedicineModel>? = null
    var detectors: List<DetectorModel>? = null
    var buyCoefficient: Float = 1.2f
    var sellCoefficient: Float = 0.8f

    fun getAllItems(): List<ItemModel> {
        val items: MutableList<ItemModel> = LinkedList()
        items.addAll(weapons!!)
        items.addAll(armors!!)
        items.addAll(artifacts!!)
        items.addAll(medicines!!)
        items.addAll(detectors!!)
        items.addAll(bullets!!)
        return items
    }
}
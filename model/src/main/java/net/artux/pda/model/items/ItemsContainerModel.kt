package net.artux.pda.model.items

import java.io.Serializable

class ItemsContainerModel : Serializable {
    var armors: List<ArmorModel>? = null
    var weapons: List<WeaponModel>? = null
    var medicines: List<MedicineModel>? = null
    var detectors: List<DetectorModel>? = null
    var artifacts: List<ArtifactModel>? = null
    var bullets: List<ItemModel>? = null
    var usual: List<ItemModel>? = null

    fun getAll(): List<ItemModel> {
        val list = mutableListOf<ItemModel>()
        list.addAll(armors as List<ItemModel>)
        list.addAll(weapons as List<ItemModel>)
        list.addAll(medicines as List<ItemModel>)
        list.addAll(artifacts as List<ItemModel>)
        list.addAll(bullets as List<ItemModel>)
        list.addAll(detectors as List<ItemModel>)
        list.addAll(usual as List<ItemModel>)
        return list
    }
}
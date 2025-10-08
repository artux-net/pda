package net.artux.pda.model.items

import java.io.Serializable

class ItemsContainerModel : Serializable {
    var armors: List<ArmorModel> = mutableListOf()
    var weapons: List<WeaponModel> = mutableListOf()
    var medicines: List<MedicineModel> = mutableListOf()
    var detectors: List<DetectorModel> = mutableListOf()
    var artifacts: List<ArtifactModel> = mutableListOf()
    var bullets: List<ItemModel> = mutableListOf()
    var usual: List<ItemModel> = mutableListOf()

    fun getAll(): List<ItemModel> {
        val list = mutableListOf<ItemModel>()
        list.addAll(armors as List<ItemModel>)
        list.addAll(weapons as List<ItemModel>)
        list.addAll(medicines as List<ItemModel>)
        list.addAll(artifacts as List<ItemModel>)
        list.addAll(bullets)
        list.addAll(detectors as List<ItemModel>)
        list.addAll(usual)
        return list
    }


    fun getByType(type: ItemType): List<ItemModel> {
        return when (type) {
            ItemType.ARMOR -> armors
            ItemType.RIFLE -> weapons.filter { it.type == type }
            ItemType.PISTOL -> weapons.filter { it.type == type }
            ItemType.MEDICINE -> medicines
            ItemType.DETECTOR -> detectors
            ItemType.ARTIFACT -> artifacts
            ItemType.BULLET -> bullets
            ItemType.ITEM -> usual
        }
    }

}
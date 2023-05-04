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
}
package net.artux.pda.model.items

data class MedicineModel(
    var stamina: Float = 0f,
    var radiation: Float = 0f,
    var health: Float = 0f
) : ItemModel()
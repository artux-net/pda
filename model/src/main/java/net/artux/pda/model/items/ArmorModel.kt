package net.artux.pda.model.items

data class ArmorModel(
    var thermalProtection: Float = 0f,
    var electricProtection: Float = 0f,
    var chemicalProtection: Float = 0f,
    var radioProtection: Float = 0f,
    var psyProtection: Float = 0f,
    var damageProtection: Float = 0f,
    var condition: Float = 0f
) : WearableModel()
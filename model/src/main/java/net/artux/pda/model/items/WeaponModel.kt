package net.artux.pda.model.items

data class WeaponModel(
    var precision: Float = 0f,
    var speed: Float = 0f,
    var damage: Float = 0f,
    var condition: Float = 0f,
    var bulletQuantity: Int = 0,
    var bulletId: Int = 0,
    var sounds: WeaponSound? = null
) : WearableModel() {

    fun getCalcPrecision(): Float{
        if (type === ItemType.PISTOL)
            return precision + 2

        return precision
    }
}
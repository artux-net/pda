package net.artux.pda.map.engine.ecs.components

import com.badlogic.ashley.core.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.map.DataRepository
import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.items.MedicineModel
import net.artux.pda.model.quest.story.StoryDataModel

class HealthComponent : Component {
    private var immortal = false
    private lateinit var armorModel: ArmorModel
    var health = 100f
    var stamina = 100f
        private set
    var radiation = 0f
    var damaged = 0f

    constructor(dataRepository: DataRepository) {
        CoroutineScope(Dispatchers.Unconfined).launch {
            dataRepository.storyDataModelFlow.collect {
                updateData(it)
            }
        }
    }

    constructor(armorModel: ArmorModel) {
        setArmorModel(armorModel)
    }

    private fun updateData(dataModel: StoryDataModel) {
        val armorModel = dataModel.getEquippedWearable(ItemType.ARMOR) as ArmorModel?
        if (armorModel != null)
            setArmorModel(dataModel.getEquippedWearable(ItemType.ARMOR) as ArmorModel)
        else
            setArmorModel(ArmorModel())
    }

    private fun setArmorModel(armorModel: ArmorModel) {
        this.armorModel = armorModel
    }

    fun setImmortal(immortal: Boolean) {
        this.immortal = immortal
    }

    fun isDead(): Boolean {
        return health < 1
    }

    fun psy(damage: Float) {
        health(calculateDamage(-damage, armorModel.psyProtection))
    }

    fun thermal(damage: Float) {
        health(calculateDamage(-damage, armorModel.thermalProtection))
    }

    fun electric(damage: Float) {
        health(calculateDamage(-damage, armorModel.electricProtection))
    }

    fun chemical(damage: Float) {
        health(calculateDamage(-damage, armorModel.chemicalProtection))
    }

    private fun calculateDamage(damage: Float, inputProtection: Float): Float {
        val protection = inputProtection * armorModel.condition / 100

        val armorDamage = if (damage < 0)
            -damage
        else
            damage

        armorModel.condition = armorModel.condition - 0.01f * armorDamage
        if (armorDamage > 0)
            damaged += armorDamage

        if (armorModel.condition > 100)
            armorModel.condition = 100f
        else if (armorModel.condition < 0)
            armorModel.condition = 0f

        return damage * ((100 - protection) / 100)
    }

    fun damage(damage: Float) {
        health(calculateDamage(-damage, armorModel.damageProtection))
    }

    fun health(value: Float) {
        if (immortal) return
        val result = health + value
        health = if (result > 0) if (result > 100) 100f else result else 0f
    }

    fun stamina(value: Float) {
        val result = stamina + value
        stamina = if (result > 0) if (result > 100) 100f else result else 0f
    }

    fun radiationValue(inputDamage: Float) {
        val damage = calculateDamage(inputDamage, armorModel.radioProtection)
        val result = radiation + damage
        radiation = if (result > 0) if (result > 100) 100f else result else 0f
    }

    fun treat(model: MedicineModel) {
        health(model.health)
        stamina += model.stamina
        radiationValue(model.radiation)
    }

    override fun toString(): String {
        return "immortal=" + immortal +
                ", health=" + health +
                ", stamina=" + stamina +
                ", radiation=" + radiation
    }
}
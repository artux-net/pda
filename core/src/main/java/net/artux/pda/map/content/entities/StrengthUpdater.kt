package net.artux.pda.map.content.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.assets.AssetManager
import net.artux.pda.map.content.ItemsGenerator
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.ecs.battle.WeaponComponent
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.items.ItemsContainerModel
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.map.Strength
import javax.inject.Inject

/**
 * Отвечает за построение сущностей на карте
 */
@PerGameMap
class StrengthUpdater @Inject constructor(
    private val itemsContainerModel: ItemsContainerModel,
    private val itemsGenerator: ItemsGenerator, private val assetManager: AssetManager
) {

    fun updateStalker(entity: Entity, strength: Strength?): Entity {
        val part =
            strength?.ordinal ?: 0

        val of = Strength.values().size
        val weapon = itemsGenerator.getSpecificFromList(itemsContainerModel.getByType(ItemType.RIFLE), part, of) as WeaponModel
        val armor = itemsGenerator.getSpecificFromList(itemsContainerModel.armors, part, of) as ArmorModel
        if (entity.getComponent(WeaponComponent::class.java) == null)
            entity.add(WeaponComponent(weapon, assetManager)
        ) else entity.getComponent(WeaponComponent::class.java).setWeaponModel(weapon)
        entity.getComponent(HealthComponent::class.java).armorModel = armor

        return entity
    }
}
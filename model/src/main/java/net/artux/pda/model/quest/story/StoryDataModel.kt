package net.artux.pda.model.quest.story

import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.ArtifactModel
import net.artux.pda.model.items.DetectorModel
import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.items.MedicineModel
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.items.WearableModel
import net.artux.pda.model.user.Gang
import net.artux.pda.model.user.GangRelation
import java.io.Serializable
import java.util.LinkedList
import java.util.Optional

data class StoryDataModel(
    var name: String? = null,
    var nickname: String? = null,
    var login: String,
    var money: Int = 0,
    var xp: Int = 0,
    var pdaId: Int = 0,
    var gang: Gang? = null,
    var relations: GangRelation? = null
) : Serializable {

    var avatar: String? = null
        get() = if (field!!.contains("http")) field else "textures/avatars/a$field.png"
    var parameters: MutableList<ParameterModel> = mutableListOf()
    var storyStates: MutableList<StoryStateModel> = mutableListOf()
    var armors: MutableList<ArmorModel> = mutableListOf()
    var weapons: MutableList<WeaponModel> = mutableListOf()
    var medicines: MutableList<MedicineModel> = mutableListOf()
    var detectors: MutableList<DetectorModel> = mutableListOf()
    var artifacts: MutableList<ArtifactModel> = mutableListOf()
    var bullets: MutableList<ItemModel> = mutableListOf()
    var items: MutableList<ItemModel> = mutableListOf()

    fun containsCurrent(): Boolean {
        return currentState != null
    }

    fun <T : ItemModel> addItem(item: T) {
        if (item.type.isCountable) {
            addAsCountable(item)
        } else {
            item.quantity = 1
            addAsNotCountable(item)
        }
    }

    private fun <T : ItemModel> addAsNotCountable(item: T) {
        val type = item.type
        if (type.isWearable) {
            val userWears = getEquippedWearable(type) != null
            (item as WearableModel).isEquipped = !userWears
        }
        item.quantity = 1
        addAsIs<T>(item)
    }

    private fun <T : ItemModel> addAsCountable(itemEntity: T) {
        val optionalItem: Optional<out ItemModel> = allItems
            .stream()
            .filter { item: ItemModel -> item.baseId == itemEntity.baseId }
            .findFirst()
        if (optionalItem.isPresent) {
            val item = optionalItem.get()
            item.quantity = item.quantity + itemEntity.quantity
        } else {
            addAsIs(itemEntity)
        }
    }

    private fun <T : ItemModel> addAsIs(itemEntity: T) {
        when (itemEntity.type) {
            ItemType.BULLET -> bullets.add(itemEntity)
            ItemType.ARMOR -> armors.add(itemEntity as ArmorModel)
            ItemType.PISTOL, ItemType.RIFLE -> weapons.add(itemEntity as WeaponModel)
            ItemType.ARTIFACT -> artifacts.add(itemEntity as ArtifactModel)
            ItemType.DETECTOR -> detectors.add(itemEntity as DetectorModel)
            ItemType.MEDICINE -> medicines.add(itemEntity as MedicineModel)
            else -> {}
        }
    }

    val currentState: StoryStateModel?
        get() {
            for (state in storyStates) {
                if (state.current) return state
            }
            return null
        }

    fun getStateByStoryId(id: Int): StoryStateModel? {
        for (state in storyStates) {
            if (state.storyId == id) return state
        }
        return null
    }

    val parametersMap: HashMap<String, Int>
        get() {
            val map = HashMap<String, Int>()
            if (parameters.size > 0) for (param in parameters) {
                map[param.key] = param.value
            }
            return map
        }

    fun getEquippedWearable(type: ItemType): WearableModel? {
        for (item in allItems) {
            if (item is WearableModel) if (item.type === type) if (item.isEquipped && item.quantity > 0) {
                return item
            }
        }
        return null
    }

    fun setCurrentWearable(itemModel: WearableModel?) {
        if (itemModel != null) {
            val current = getEquippedWearable(itemModel.type)
            if (current != null) current.isEquipped = false

            if (current !== itemModel) itemModel.isEquipped = true
        }
    }

    fun getItemByBaseId(baseId: Int): ItemModel? {
        for (item in allItems) {
            if (item.baseId == baseId) {
                return item
            }
        }
        return null
    }

    val allItems: List<ItemModel>
        get() {
            val items: MutableList<ItemModel> = LinkedList()
            items.addAll(weapons)
            items.addAll(armors)
            items.addAll(artifacts)
            items.addAll(medicines)
            items.addAll(detectors)
            items.addAll(bullets)
            items.addAll(this.items)
            return items
        }
    val totalWeight: Float
        get() {
            var weight = 0f
            for (item in allItems) weight += item.weight * item.quantity
            return weight
        }

    val rang: Rang
        get() = getRang(xp)

    enum class Rang(
        val id: Int,
        val xp: Int,
        val isLast: Boolean = false
    ) {
        BEGINNER(0, 0), NEW(1, 1000), STALKER(2, 3000), EXPERIENCE(3, 6000), OLD(
            4,
            10000
        ),
        MASTER(5, 16000), FINAL(6, Int.MAX_VALUE, true);

        val nextRang: Rang?
            get() = if (id < values().size - 1) values()[id + 1] else null
    }

    companion object {
        @JvmStatic
        fun getRang(xp: Int): Rang {
            var previousRang = Rang.BEGINNER
            for (rang in Rang.values()) {
                previousRang = if (rang.xp > xp) return previousRang else rang
            }
            return Rang.FINAL
        }

    }
}
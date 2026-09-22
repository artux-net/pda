package net.artux.pda.flow.network.dto

import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.ChapterModel
import net.artux.pda.model.quest.Stage
import net.artux.pda.model.quest.StoryItem
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.Text
import net.artux.pda.model.quest.Transfer
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.model.quest.story.StoryStateModel
import net.artux.pda.model.user.Gang
import net.artux.pda.model.user.GangRelation

// Hand-written DTO -> :model mapping for the iOS flow, standing in for the MapStruct-generated
// StoryMapper/StageMapper/ItemMapper that only exist in :app (tied to the Retrofit-generated
// net.artux.pdanetwork.model.* DTOs, not usable from :core). Field-for-field equivalent of
// what those mappers do for the subset this flow actually needs.

fun StoryInfoDto.toModel(): StoryItem = StoryItem(
    id = (id ?: 0L).toInt(),
    title = title ?: "",
    icon = icon,
    desc = desc ?: "",
    complete = false,
    needs = needs ?: emptyList()
)

fun StoryDto.toModel(): StoryModel {
    val model = StoryModel(id = id ?: -1L, title = title ?: "")
    chapters?.forEach { (key, dto) -> model.chapters[key] = dto.toModel() }
    return model
}

fun ChapterDto.toModel(): ChapterModel {
    val model = ChapterModel()
    val stagesMap = HashMap<Long, Stage>()
    stages?.values?.forEach { dto ->
        val stage = dto.toModel()
        stagesMap[stage.id] = stage
    }
    model.stages = stagesMap
    return model
}

fun StageDto.toModel(): Stage {
    val stage = Stage()
    stage.id = id ?: 0L
    stage.typeStage = typeStage ?: 0
    stage.background = background
    stage.title = title
    stage.message = message
    stage.typeMessage = typeMessage
    stage.texts = texts?.map { it.toModel() } ?: emptyList()
    stage.transfers = transfers?.map { it.toModel() } ?: emptyList()
    stage.actions = actions?.let { HashMap(it) } ?: HashMap()
    stage.data = data?.let { HashMap(it) } ?: HashMap()
    return stage
}

fun TransferDto.toModel(): Transfer {
    val transfer = Transfer()
    transfer.stage = (stage ?: 0L).toInt()
    transfer.text = text
    transfer.condition = condition?.let { HashMap(it) }
    return transfer
}

fun TextDto.toModel(): Text {
    val t = Text()
    t.text = text ?: ""
    t.condition = condition?.let { HashMap(it) } ?: HashMap()
    return t
}

fun GameMapDto.toModel(): GameMap = GameMap(
    id = id ?: 0L,
    title = title ?: "Карта",
    tmx = tmx ?: "kordon.tmx",
    defPos = defPos ?: "500:500"
)

fun StoryStateDto.toModel(): StoryStateModel {
    val state = StoryStateModel()
    state.current = current ?: false
    state.over = over ?: false
    state.storyId = storyId ?: 0
    state.chapterId = chapterId ?: 0
    state.stageId = stageId ?: 0
    return state
}

fun GangRelationDto.toModel(): GangRelation {
    val relation = GangRelation()
    relation.loners = loners ?: 0
    relation.bandits = bandits ?: 0
    relation.military = military ?: 0
    relation.liberty = liberty ?: 0
    relation.duty = duty ?: 0
    relation.monolith = monolith ?: 0
    relation.mercenaries = mercenaries ?: 0
    relation.scientists = scientists ?: 0
    relation.clearSky = clearSky ?: 0
    return relation
}

fun ItemDto.toModel(): ItemModel {
    val item = ItemModel()
    item.type = ItemType.getByTypeId(type ?: 7)
    item.icon = icon
    item.title = title
    item.baseId = baseId ?: 0
    item.weight = weight ?: 0f
    item.price = price ?: 0
    item.quantity = quantity ?: 0
    return item
}

fun WeaponDto.toModel(): WeaponModel {
    val weapon = WeaponModel(
        precision = precision ?: 0f,
        speed = speed ?: 0f,
        damage = damage ?: 0f,
        condition = condition ?: 0f,
        bulletQuantity = bulletQuantity ?: 0,
        bulletId = bulletId ?: 0,
        distance = distance ?: 0f,
        sounds = null
    )
    weapon.type = ItemType.getByTypeId(type ?: 0)
    weapon.icon = icon
    weapon.title = title
    weapon.baseId = baseId ?: 0
    weapon.weight = weight ?: 0f
    weapon.price = price ?: 0
    weapon.quantity = quantity ?: 0
    weapon.isEquipped = equipped ?: false
    return weapon
}

fun ArmorDto.toModel(): ArmorModel {
    val armor = ArmorModel(
        thermalProtection = thermalProtection ?: 0f,
        electricProtection = electricProtection ?: 0f,
        chemicalProtection = chemicalProtection ?: 0f,
        radioProtection = radioProtection ?: 0f,
        psyProtection = psyProtection ?: 0f,
        damageProtection = damageProtection ?: 0f,
        condition = condition ?: 0f
    )
    armor.type = ItemType.ARMOR
    armor.icon = icon
    armor.title = title
    armor.baseId = baseId ?: 0
    armor.weight = weight ?: 0f
    armor.price = price ?: 0
    armor.quantity = quantity ?: 0
    armor.isEquipped = equipped ?: false
    return armor
}

fun StoryDataDto.toModel(): StoryDataModel {
    val model = StoryDataModel(
        name = name,
        nickname = nickname,
        login = login,
        money = money ?: 0,
        xp = xp ?: 0,
        pdaId = pdaId ?: 0,
        gang = gang?.let { Gang.ofId(it) } ?: Gang.LONERS,
        relations = relations?.toModel() ?: GangRelation()
    )
    // avatar's getter does field!!.contains("http") - a null field NPEs the moment anything
    // reads it (see MockDataFactory's own comment on this same gotcha), so always set it.
    model.avatar = avatar ?: "0"
    model.storyStates = (storyStates?.map { it.toModel() } ?: emptyList()).toMutableList()
    weapons?.forEach { model.weapons.add(it.toModel()) }
    armors?.forEach { model.armors.add(it.toModel()) }
    bullets?.forEach { model.bullets.add(it.toModel()) }
    items?.forEach { model.items.add(it.toModel()) }
    return model
}

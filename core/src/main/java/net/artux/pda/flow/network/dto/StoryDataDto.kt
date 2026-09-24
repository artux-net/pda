package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Mirrors net.artux.pdanetwork.model.StoryData (GET api/v1/user/quest/info, and the response
// of PUT api/v1/quest/commands) - only the fields the flow needs to drive dialogue/map state.
data class StoryDataDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("login") val login: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("money") val money: Int? = null,
    @SerializedName("xp") val xp: Int? = null,
    @SerializedName("pdaId") val pdaId: Int? = null,
    // Enum name ("LONERS"), not Gang's numeric id - see app/api.json's StoryData schema.
    @SerializedName("gang") val gang: String? = null,
    @SerializedName("relations") val relations: GangRelationDto? = null,
    // What QuestUtil.check() evaluates transfer/text conditions against.
    @SerializedName("parameters") val parameters: List<ParameterDto>? = null,
    @SerializedName("storyStates") val storyStates: List<StoryStateDto>? = null,
    @SerializedName("weapons") val weapons: List<WeaponDto>? = null,
    @SerializedName("armors") val armors: List<ArmorDto>? = null,
    @SerializedName("bullets") val bullets: List<ItemDto>? = null,
    @SerializedName("items") val items: List<ItemDto>? = null
)

data class GangRelationDto(
    @SerializedName("loners") val loners: Int? = null,
    @SerializedName("bandits") val bandits: Int? = null,
    @SerializedName("military") val military: Int? = null,
    @SerializedName("liberty") val liberty: Int? = null,
    @SerializedName("duty") val duty: Int? = null,
    @SerializedName("monolith") val monolith: Int? = null,
    @SerializedName("mercenaries") val mercenaries: Int? = null,
    @SerializedName("scientists") val scientists: Int? = null,
    @SerializedName("clearSky") val clearSky: Int? = null
)

data class ParameterDto(
    @SerializedName("key") val key: String? = null,
    @SerializedName("value") val value: Int? = null
)

data class StoryStateDto(
    @SerializedName("current") val current: Boolean? = null,
    @SerializedName("over") val over: Boolean? = null,
    @SerializedName("storyId") val storyId: Int? = null,
    @SerializedName("chapterId") val chapterId: Int? = null,
    @SerializedName("stageId") val stageId: Int? = null
)

data class ItemDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("baseId") val baseId: Int? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null
)

data class WeaponDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("baseId") val baseId: Int? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("precision") val precision: Float? = null,
    @SerializedName("speed") val speed: Float? = null,
    @SerializedName("damage") val damage: Float? = null,
    @SerializedName("condition") val condition: Float? = null,
    @SerializedName("bulletQuantity") val bulletQuantity: Int? = null,
    @SerializedName("bulletId") val bulletId: Int? = null,
    @SerializedName("distance") val distance: Float? = null,
    @SerializedName("equipped") val equipped: Boolean? = null
)

data class ArmorDto(
    @SerializedName("type") val type: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("baseId") val baseId: Int? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("price") val price: Int? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("thermalProtection") val thermalProtection: Float? = null,
    @SerializedName("electricProtection") val electricProtection: Float? = null,
    @SerializedName("chemicalProtection") val chemicalProtection: Float? = null,
    @SerializedName("radioProtection") val radioProtection: Float? = null,
    @SerializedName("psyProtection") val psyProtection: Float? = null,
    @SerializedName("damageProtection") val damageProtection: Float? = null,
    @SerializedName("condition") val condition: Float? = null,
    @SerializedName("equipped") val equipped: Boolean? = null
)

// Request body for PUT api/v1/quest/commands - mirrors net.artux.pdanetwork.model.CommandBlock.
data class CommandBlockDto(
    @SerializedName("actions") val actions: Map<String, List<String>>
)

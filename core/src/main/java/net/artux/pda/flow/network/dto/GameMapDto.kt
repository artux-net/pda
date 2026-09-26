package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Mirrors net.artux.pdanetwork.model.GameMap (GET api/v1/quest/maps/{storyId}/{mapId}).
data class GameMapDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("level") val level: Int? = null,
    @SerializedName("tmx") val tmx: String? = null,
    @SerializedName("defPos") val defPos: String? = null,
    @SerializedName("points") val points: List<PointDto>? = null,
    @SerializedName("spawns") val spawns: List<SpawnDto>? = null
)

// Mirrors net.artux.pda.model.map.Point - quest markers, sellers, caches and map transfers.
// id is a UUID on the wire; kept as String here (Gson has no default UUID adapter) and parsed
// in the mapper, same defensive-default treatment as every other DTO in this package.
data class PointDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("pos") val pos: String? = null,
    @SerializedName("data") val data: Map<String, String>? = null,
    @SerializedName("actions") val actions: Map<String, List<String>>? = null,
    @SerializedName("condition") val condition: Map<String, List<String>>? = null
)

// Mirrors net.artux.pda.model.map.SpawnModel - a gang's (Loners/Bandits/...) presence on the
// map, which the map's SpawnController turns into stalker groups.
data class SpawnDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    // Enum name ("BANDITS"), not Gang's numeric id - same wire format as StoryDataDto.gang.
    @SerializedName("group") val group: String? = null,
    @SerializedName("strength") val strength: String? = null,
    @SerializedName("r") val r: Int? = null,
    @SerializedName("n") val n: Int? = null,
    @SerializedName("pos") val pos: String? = null,
    @SerializedName("data") val data: Map<String, List<String>>? = null,
    @SerializedName("actions") val actions: Map<String, List<String>>? = null,
    @SerializedName("condition") val condition: Map<String, List<String>>? = null
)

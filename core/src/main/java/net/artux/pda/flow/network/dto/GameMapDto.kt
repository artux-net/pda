package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Mirrors net.artux.pdanetwork.model.GameMap (GET api/v1/quest/maps/{storyId}/{mapId}).
// points/spawns are intentionally left unmapped for now (out of scope - see plan); the map
// itself renders fine without them, they only affect spawn placement/quest markers.
data class GameMapDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("tmx") val tmx: String? = null,
    @SerializedName("defPos") val defPos: String? = null
)

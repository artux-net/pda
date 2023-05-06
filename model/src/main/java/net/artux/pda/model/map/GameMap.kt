package net.artux.pda.model.map

import java.io.Serializable

data class GameMap(
    var id: Long,
    var title: String,
    var tmx: String,
    var defPos: String = "500:500",
    var points: List<Point>? = mutableListOf(),
    var spawns: List<SpawnModel>? = mutableListOf()
) : Serializable
package net.artux.pda.model.map

import java.io.Serializable

data class GameMap(
    var id: Long = 0,
    var title: String = "Карта",
    var level: Int = 0,
    var tmx: String = "kordon.tmx",
    var defPos: String = "500:500",
    var points: List<Point> = mutableListOf(),
    var spawns: List<SpawnModel> = mutableListOf()
) : Serializable
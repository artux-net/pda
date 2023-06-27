package net.artux.pda.model.map

import net.artux.pda.model.user.Gang
import java.io.Serializable
import java.util.Collections

data class SpawnModel(
    var id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    var group: Gang? = null,
    var strength: Strength? = null,
    var r: Int = 0,
    var n: Int = 0,
    var pos: String = "500:500"
) : Serializable {

    var data: Map<String, List<String>> = mutableMapOf()
    var actions: Map<String, List<String>> = mutableMapOf()
    var condition: Map<String, List<String>> = mutableMapOf()

    fun getParams(): Set<String> {
        return data.keys
    }
}
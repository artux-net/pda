package net.artux.pda.model.map

import net.artux.pda.model.user.Gang
import java.io.Serializable

data class SpawnModel(
    var id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    var group: Gang? = null,
    var strength: Strength? = null,
    var r: Int = 0,
    var n: Int = 0,
    var pos: String? = null,
    var data: HashMap<String, List<String>>? = null,
    var actions: HashMap<String, List<String>>? = null,
    var condition: HashMap<String, List<String>>? = null,
) : Serializable {

    fun getParams(): Set<String> {
        if (data == null)
            data = HashMap()
        return data!!.keys
    }
}
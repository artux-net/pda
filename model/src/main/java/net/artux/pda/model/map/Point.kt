package net.artux.pda.model.map

import java.io.Serializable

data class Point(
    var type: Int = 0,
    var name: String? = null,
    var pos: String? = null,
    var data: HashMap<String, String> = HashMap(),
    var condition: HashMap<String, List<String>> = HashMap()
) : Serializable
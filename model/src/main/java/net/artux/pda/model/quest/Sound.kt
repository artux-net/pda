package net.artux.pda.model.quest

import java.io.Serializable

class Sound : Serializable {
    var id = 0
    var type = 0
    var name: String? = null
    var url: String? = null
    var params: Array<String> = arrayOf()
}
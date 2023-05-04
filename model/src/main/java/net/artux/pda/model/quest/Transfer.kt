package net.artux.pda.model.quest

import java.io.Serializable

class Transfer : Serializable {
    var stage = 0
    var text: String? = null
    var condition: HashMap<String, List<String>>? = null
}
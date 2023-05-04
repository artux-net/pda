package net.artux.pda.model.quest

import java.io.Serializable

class Text : Serializable {
    var text: String = ""
    var condition: HashMap<String, List<String>> = HashMap()
}
package net.artux.pda.model.quest

import java.io.Serializable

class Stage : Serializable {
    var id: Int? = null
    var typeStage: Int? = null
    var background: String? = null
    var title: String? = null
    var message: String? = null
    var typeMessage: Int? = null
    var texts: List<Text>? = listOf()
    var transfers: List<Transfer>? = listOf()
    var actions: HashMap<String, List<String>>? = HashMap()
    var data: HashMap<String, String>? = HashMap()
}
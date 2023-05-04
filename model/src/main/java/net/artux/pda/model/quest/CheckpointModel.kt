package net.artux.pda.model.quest

import java.io.Serializable

class CheckpointModel : Serializable {
    var parameter: String? = null
    var title: String? = null
    var chapter: Int? = null
    var stage: Int? = null
    fun isActual(vararg params: String): Boolean {
        for (param in params) {
            if (param == parameter) return true
        }
        return false
    }
}
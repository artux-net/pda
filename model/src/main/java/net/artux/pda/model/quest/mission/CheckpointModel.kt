package net.artux.pda.model.quest.mission

import java.io.Serializable

data class CheckpointModel(
    val parameter: String,
    val title: String,
    val type: CheckpointType,
    val condition : Map<String, List<String>>, // условие выполнения, по выполнении добавляется параметр следующего чека
    val actions: Map<String, List<String>> //действия, выполняемые по достижении условия, кроме добавления следующего параметра и удаления текущего
) : Serializable {

    fun isActual(vararg params: String): Boolean {
        for (param in params) {
            if (param == parameter) return true
        }
        return false
    }
}
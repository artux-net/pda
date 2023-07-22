package net.artux.pda.model.quest

import java.io.Serializable

class ChapterModel : Serializable {
    var title = ""
    var stages: Map<Long, Stage> = mapOf()

    fun getStage(id: Long): Stage? {
        return stages[id]
    }
}
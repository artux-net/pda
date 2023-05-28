package net.artux.pda.model.quest

import java.io.Serializable

class ChapterModel : Serializable {
    var stages: Map<Long, Stage> = mapOf()
    //var points: HashMap<Stage> = listOf()
    fun getStage(id: Long): Stage? {
        return stages[id]
    }
}
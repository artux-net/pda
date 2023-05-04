package net.artux.pda.model.quest

import java.io.Serializable

class ChapterModel : Serializable {
    var stages: List<Stage> = listOf()
    var music: List<Sound> = listOf()
    fun getStage(id: Int): Stage? {
        for (stage in stages) {
            if (stage.id == id) return stage
        }
        return null
    }
}
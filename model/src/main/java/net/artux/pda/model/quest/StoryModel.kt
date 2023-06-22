package net.artux.pda.model.quest

import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.mission.MissionModel
import java.io.Serializable
import java.util.LinkedList

class StoryModel : Serializable {
    var id: Long? = null
    var title: String = ""
    val maps: MutableMap<Long, GameMap> = mutableMapOf()
    var chapters: MutableMap<String, ChapterModel> = mutableMapOf()
    var missions: MutableList<MissionModel> = mutableListOf()

    fun getChapter(id: String): ChapterModel? {
        return chapters[id]
    }

    fun getMap(id: Long): GameMap? {
        return maps[id]
    }

    fun getCurrentMissions(vararg params: String): List<MissionModel> {
        val missionModels: MutableList<MissionModel> = LinkedList()
        for (missionModel in missions) {
            if (missionModel.hasParams(*params)) missionModels.add(missionModel)
        }
        return missionModels
    }

}
package net.artux.pda.model.quest

import net.artux.pda.model.map.GameMap
import net.artux.pda.model.map.Point
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
        if (missions != null) for (missionModel in missions) {
            if (missionModel.hasParams(*params)) missionModels.add(missionModel)
        }
        return missionModels
    }

    fun getCurrentMission(param: String?): MissionModel? {
        for (missionModel in missions!!) {
            if (missionModel.hasParams(param!!)) return missionModel
        }
        return null
    }

    fun findPathWithinMission(param: String?, currentMap: GameMap?): Point? {
        val missionModel = getCurrentMission(param)
        if (missionModel != null) {
            val checkpointModel = missionModel.getCurrentCheckpoint(param!!)
            var targetPoint: Point?
            var targetMap: GameMap?
            for (map in maps!!.values) {
                for (point in map.points!!) {
                    val data: Map<String, String>? = point.data
                    val chapterString = data!!["chapter"]
                    val stageString = data["stage"]
                    if (chapterString != null && stageString != null && chapterString == checkpointModel!!.chapter.toString() && stageString == checkpointModel.stage.toString()) {
                        targetMap = map
                        targetPoint = point
                    }
                }
            }
        }
        return null
    }
}
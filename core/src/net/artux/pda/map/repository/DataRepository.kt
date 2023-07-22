package net.artux.pda.map.repository

import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.model.QuestUtil
import net.artux.pda.model.items.ItemsContainerModel
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel
import org.apache.commons.lang3.SerializationUtils
import org.luaj.vm2.LuaTable
import java.util.*
import kotlin.collections.LinkedHashMap


class DataRepository(
    val platformInterface: PlatformInterface,
    var initDataModel: StoryDataModel,
    var gameMap: GameMap,
    val items: ItemsContainerModel,
    val storyModel: StoryModel,
    val properties: Properties,
    val applicationLogger: ApplicationLogger
) {
    val TAG = "Data Repository"

    data class Builder(
        var platformInterface: PlatformInterface? = null,
        var luaTable: LuaTable? = null,
        var storyDataModel: StoryDataModel? = null,
        var gameMap: GameMap? = null,
        var items: ItemsContainerModel? = null,
        var storyModel: StoryModel? = null,
        var properties: Properties? = null,
        var applicationLogger: ApplicationLogger? = null
    ) {


        fun platformInterface(platformInterface: PlatformInterface) =
            apply { this.platformInterface = platformInterface }

        fun storyDataModel(storyDataModel: StoryDataModel) =
            apply { this.storyDataModel = storyDataModel }

        fun gameMap(gameMap: GameMap) = apply { this.gameMap = gameMap }
        fun items(items: ItemsContainerModel) = apply { this.items = items }
        fun storyModel(storyModel: StoryModel) = apply { this.storyModel = storyModel }
        fun properties(properties: Properties) = apply { this.properties = properties }
        fun build() = DataRepository(
            platformInterface!!,
            storyDataModel!!,
            gameMap!!,
            items!!,
            storyModel!!,
            properties!!,
            //luaTable!!,
            applicationLogger!!
        )
    }

    var currentStoryDataModel = initDataModel
    private val dataModelFlow: MutableSharedFlow<StoryDataModel> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val storyDataModelFlow: SharedFlow<StoryDataModel> = dataModelFlow

    var updated: Boolean = true

    init {
        dataModelFlow.tryEmit(initDataModel)
    }

    fun setUserData(storyDataModel: StoryDataModel) {
        Gdx.app.applicationLogger.log(TAG, "Story data updated.")
        this.initDataModel = SerializationUtils.clone(storyDataModel)
        currentStoryDataModel = storyDataModel
        dataModelFlow.tryEmit(storyDataModel)
    }

    fun sendData(map: Map<String, String>) {
        applyActions(LinkedHashMap())
        platformInterface.send(map)
    }

    fun update() {
        dataModelFlow.tryEmit(currentStoryDataModel)
    }

    fun applyActions(actions: Map<String, List<String>>?, calculateDiff: Boolean = true) {
        val summaryMap = if (calculateDiff)
            QuestUtil.calculateDifference(initDataModel, currentStoryDataModel)
        else
            LinkedHashMap()
        if (actions != null) {
            summaryMap.putAll(actions)
            summaryMap["syncNow"] = emptyList()
            platformInterface.applyActions(summaryMap)
        }
    }

    fun applyActions(actions: Map<String, List<String>>?) {
        applyActions(actions, true)
    }
}
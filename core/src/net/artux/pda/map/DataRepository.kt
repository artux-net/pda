package net.artux.pda.map

import kotlinx.coroutines.flow.MutableStateFlow
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.model.QuestUtil
import net.artux.pda.model.items.ItemsContainerModel
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel
import java.util.*


class DataRepository(
    val platformInterface: PlatformInterface,
    private val storyDataModel: StoryDataModel,
    var gameMap: GameMap,
    val items: ItemsContainerModel,
    val storyModel: StoryModel,
    val properties: Properties
) {


    data class Builder(
        var platformInterface: PlatformInterface? = null,
        var storyDataModel: StoryDataModel? = null,
        var gameMap: GameMap? = null,
        var items: ItemsContainerModel? = null,
        var storyModel: StoryModel? = null,
        var properties: Properties? = null
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
            properties!!
        )
    }

    private var oldStoryDataModel = storyDataModel
    var storyDataModelFlow: MutableStateFlow<StoryDataModel> = MutableStateFlow(storyDataModel)

    fun setStoryDataModel(storyDataModel: StoryDataModel) {
        oldStoryDataModel = storyDataModelFlow.value
        storyDataModelFlow.value = storyDataModel
    }

    fun getCurrentStoryDataModel(): StoryDataModel {
        return storyDataModelFlow.value
    }

    fun sendData(map: Map<String, String>) {
        applyActions(emptyMap())
        platformInterface.send(map)
    }

    fun applyActions(actions: Map<String, List<String>>?) {
        val summaryMap = HashMap(QuestUtil.difference(oldStoryDataModel, storyDataModel))
        if (actions != null && actions.isNotEmpty()) {
            summaryMap.putAll(actions)
            platformInterface.applyActions(summaryMap)
        }
    }
}
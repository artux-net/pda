package net.artux.pda.map.repository

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import net.artux.pda.common.ActionHandler
import net.artux.pda.map.engine.EngineManager
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.map.utils.di.scope.PerGameMap
import net.artux.pda.model.QuestUtil
import net.artux.pda.model.items.ItemsContainerModel
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel
import org.apache.commons.lang3.SerializationUtils
import java.util.*
import javax.inject.Inject

@PerGameMap
class EntitySaver @Inject constructor(
    val platformInterface: PlatformInterface
) {
    fun save() {
        TODO("Not yet implemented")
    }


}
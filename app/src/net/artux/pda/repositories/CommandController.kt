package net.artux.pda.repositories

import androidx.lifecycle.MutableLiveData
import com.google.android.datatransport.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.ui.viewmodels.event.OpenStageEvent
import net.artux.pda.ui.viewmodels.event.ScreenDestination
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import net.artux.pda.utils.AdType
import net.artux.pdanetwork.model.CommandBlock
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CommandController @Inject constructor(
    val soundManager: QuestSoundManager,
    var mapper: StoryMapper,
    val repository: QuestRepository
) {

    val sellerEvent: SingleLiveEvent<Event<Int>> = SingleLiveEvent()
    val stageEvent: SingleLiveEvent<Event<OpenStageEvent>> = SingleLiveEvent()
    val exitEvent: SingleLiveEvent<ScreenDestination> = SingleLiveEvent()
    val adEvent: SingleLiveEvent<AdType> = SingleLiveEvent()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var cacheCommands: MutableMap<String, MutableList<String>> = LinkedHashMap()

    val storyData: MutableLiveData<StoryDataModel> = MutableLiveData()
    val status: SingleLiveEvent<StatusModel> = SingleLiveEvent()
    val commandsToSync: List<String> = listOf("exitStory", "finishStory", "syncNow", "showAd")

    var needSync = false

    fun process(commands: Map<String, List<String>>?) = scope.launch {
        if (commands.isNullOrEmpty())
            return@launch

        for (command in commands) {
            when (command.key) {
                "openSeller" -> openSeller(command.value)
                "openStage" -> openStage(command.value)
                "loopMusic" -> soundManager.playMusic(command.value.first(), true)
                "playMusic" -> soundManager.playMusic(command.value.first(), false)
                "playSound" -> soundManager.playSound(command.value.first())
                "pauseSound" -> soundManager.pauseSound(command.value.first())
                "stopMusic" -> soundManager.stop()
                "pauseAllSound" -> soundManager.pause()
                "resumeAllSound" -> soundManager.resume()
                "showAd" -> showAd(command.value)
                "script", "lua" -> runLua(command.value)

                else -> {
                    // * commands for server * //
                    // addCommand(command.key, command.value)
                    if (command.key in commandsToSync)
                        needSync = true
                }

            }
        }

        if (!needSync)
            return@launch

        syncNow()
            .onSuccess {
                // * postprocessing * //
                for (command in commands)
                    when (command.key) {
                        "exitStory" -> exitStory()
                        "finishStory" -> exitStory()
                    }
            }
    }

    fun clearCache() {
        cacheCommands.clear()
    }

    fun cacheCommands(commands: Map<String, List<String>>) {
        for (command in commands) {
            cacheCommand(command.key, command.value)
        }
    }

    private fun cacheCommand(key: String, params: List<String>) {
        val current = cacheCommands.getOrDefault(key, mutableListOf())
        current.addAll(params)
        cacheCommands[key] = current
    }


    private fun openSeller(args: List<String>) {
        sellerEvent.postValue(Event.ofData(args.first().toInt()))
    }

    private fun openStage(list: List<String>) {
        val stageId: Long
        val chapterId: Int
        if (list.size > 2)
            return
        else if (list.size == 1) {
            stageId = list[0].toLong()
            stageEvent.postValue(Event.ofData(OpenStageEvent(stageId)))
        } else {
            chapterId = list[0].toInt()
            stageId = list[1].toLong()
            stageEvent.postValue(Event.ofData(OpenStageEvent(stageId, chapterId)))
        }
    }

    private fun exitStory() {
        exitEvent.postValue(ScreenDestination.STORIES)
    }

    fun checkStage(currentStoryId: Int, currentChapterId: Int, currentStageId: Long) {
        var states = cacheCommands["state"]
        if (states == null)
            states = LinkedList()
        states.add("$currentStoryId:$currentChapterId:${currentStageId}")
        cacheCommands["state"] = states
    }

    suspend fun syncNow(commands: Map<String, List<String>>): Result<StoryDataModel> =
        repository.syncMember(CommandBlock().actions(commands))
            .map { mapper.dataModel(it) }
            .onSuccess {
                needSync = false
                storyData.postValue(it)
            }
            .onFailure { status.postValue(StatusModel(it)) }

    suspend fun syncNow(): Result<StoryDataModel> =
        syncNow(cacheCommands)
            .onSuccess {
                cacheCommands = LinkedHashMap()// reset sync map
            }

    fun resetData() = scope.launch {
        repository.resetData()
            .map { mapper.dataModel(it) }
            .onSuccess {
                storyData.postValue(it)
                status.postValue(StatusModel("Ok"))
            }
            .onFailure { status.postValue(StatusModel(it)) }
    }

    fun showAd(types: List<String>) {
        if (types.isEmpty())
            adEvent.postValue(AdType.USUAL)
        else
            showAd(types.first())
    }

    fun showAd(type: String) {
        when (type) {
            "video" -> adEvent.postValue(AdType.VIDEO)
            "usual" -> adEvent.postValue(AdType.USUAL)
            else -> adEvent.postValue(AdType.USUAL)
        }
    }

    fun processWithServer(actions: Map<String, List<String>>) = scope.launch {
        syncNow(actions)
    }

    private fun runLua(scripts: List<String>) {
        val globals = JsePlatform.standardGlobals()
        globals.set("commandController", CoerceJavaToLua.coerce(this))
        globals.set("soundManager", CoerceJavaToLua.coerce(soundManager))
        for (script in scripts) {
            val chunk = globals.load(script)
            chunk.call()
        }
    }
}
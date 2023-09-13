package net.artux.pda.repositories

import androidx.lifecycle.MutableLiveData
import com.google.android.datatransport.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.commands.Commands
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StageMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.NotificationModel
import net.artux.pda.model.quest.NotificationType
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.scripting.ICommandController
import net.artux.pda.scripting.LuaController
import net.artux.pda.ui.viewmodels.event.OpenStageEvent
import net.artux.pda.ui.viewmodels.event.ScreenDestination
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import net.artux.pda.utils.AdType
import net.artux.pdanetwork.model.CommandBlock
import org.luaj.vm2.Globals
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CommandController @Inject constructor(
    val soundManager: QuestSoundManager,
    var mapper: StoryMapper,
    var stageMapper: StageMapper,
    val repository: QuestRepository
) : ICommandController {

    val sellerEvent: SingleLiveEvent<Event<Int>> = SingleLiveEvent()
    val stageEvent: SingleLiveEvent<Event<OpenStageEvent>> = SingleLiveEvent()
    val exitEvent: SingleLiveEvent<ScreenDestination> = SingleLiveEvent()
    val adEvent: SingleLiveEvent<AdType> = SingleLiveEvent()
    var notification: SingleLiveEvent<NotificationModel> = SingleLiveEvent()
    val luaController: LuaController = LuaController()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var cacheCommands: MutableMap<String, MutableList<String>> = LinkedHashMap()

    val storyData: MutableLiveData<StoryDataModel> = MutableLiveData()
    val status: SingleLiveEvent<StatusModel> = SingleLiveEvent()

    var needSync = false

    init {
        luaController.putObjectToScriptContext("controller", this)
        luaController.putObjectToScriptContext("soundManager", soundManager)
    }

    override fun processWithServer(actions: Map<String, List<String>>){
        needSync = true
        cacheCommands(actions)
        process(actions)
    }

    override fun process(commands: Map<String, List<String>>?) {
        if (commands.isNullOrEmpty()) return

        for (command in commands){
            when (command.key){
                "script", "lua" -> luaController.runLua(command.value)
            }
        }

        scope.launch {
            for (command in commands) {
                when (command.key) {
                    "showNotification", "openNotification" -> openNotification(command.value)
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
                    "asyncScript", "asyncLua" -> luaController.runLua(command.value)

                    else -> {
                        // * commands for server * //
                        // addCommand(command.key, command.value)
                        if (command.key in Commands.commandsToSync) {
                            needSync = true
                        }
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
    }

    override fun getLuaGlobals(): Globals = luaController.getLuaGlobals()

    override fun openNotification(args: List<String>) {
        if (args.size == 2)
            notification.postValue(
                stageMapper.notification(
                    NotificationType.ALERT,
                    "${args[0]}:${args[1]}",
                    storyData.value
                )
            )
    }

    override fun openNotification(title: String, message: String) {
        notification.postValue(
            stageMapper.notification(
                NotificationType.ALERT,
                "${title}:${message}",
                storyData.value
            )
        )
    }

    fun clearCache() {
        cacheCommands.clear()
    }

    fun cacheCommands(commands: Map<String, List<String>>) {
        for (command in commands) {
            if (command.key in Commands.serverCommands)
                cacheCommand(command.key, command.value)
        }
    }

    fun cacheCommand(key: String, params: List<String>) {
        val current = cacheCommands.getOrDefault(key, mutableListOf())
        current.addAll(params)
        cacheCommands[key] = current
    }

    override fun openSeller(args: List<String>) {
        sellerEvent.postValue(Event.ofData(args.first().toInt()))
    }

    override fun openSeller(sellerId: Int) {
        sellerEvent.postValue(Event.ofData(sellerId))
    }

    override fun openStage(list: List<String>) {
        if (list.size > 2)
            return

        val stageId: Int
        val chapterId: Int
        if (list.size == 1)
            openStage(list.first())
        else {
            chapterId = list[0].toInt()
            stageId = list[1].toInt()
            openStage(chapterId,stageId)
        }
    }

    override fun openStage(arg: String) {
        if (arg.contains(":")) {
            val args = arg.split(":")
            val chapterId = args.first().toInt()
            val stageId = args[1].toInt()
            openStage(chapterId,stageId)
        } else {
            val stageId = arg.toInt()
            openStage(stageId)
        }
    }


    override fun openStage(chapterId: Int, stageId: Int) {
        stageEvent.postValue(Event.ofData(OpenStageEvent(stageId.toLong(), chapterId)))
    }

    override fun openStage(stageId: Int) {
        stageEvent.postValue(Event.ofData(OpenStageEvent(stageId.toLong())))
    }

    fun exitStory() {
        exitEvent.postValue(ScreenDestination(ScreenDestination.STORIES))
    }

    fun checkStage(currentStoryId: Int, currentChapterId: Int, currentStageId: Long) {
        var states = cacheCommands["state"]
        if (states == null)
            states = LinkedList()
        states.add("$currentStoryId:$currentChapterId:${currentStageId}")
        cacheCommands["state"] = states
    }

    var syncTries = 0
    suspend fun syncNow(commands: Map<String, List<String>>): Result<StoryDataModel> =
        repository.syncMember(CommandBlock().actions(commands))
            .also { syncTries++ }
            .map { mapper.dataModel(it) }
            .onSuccess {
                needSync = false
                storyData.postValue(it)
                syncTries = 0
            }
            .onFailure {
                if (syncTries > 5) {
                    status.postValue(StatusModel("Невозможно синхронизировать, перезапустите ПДА или установите стабильное интернет-соединение"))
                    return@onFailure
                }
                status.postValue(StatusModel("Ошибка синхронизации, повтор..."))
                Timber.w(it, "Ошибка синхронизации, повтор попытки через 5 секунд")
                Thread.sleep(1000 * 3)
                syncNow(commands)
            }

    suspend fun syncNow(): Result<StoryDataModel> =
        syncNow(cacheCommands)
            .onSuccess {
                cacheCommands = LinkedHashMap()// reset sync map
            }
            .onFailure {}

    fun resetData() = scope.launch {
        repository.resetData()
            .map { mapper.dataModel(it) }
            .onSuccess {
                storyData.postValue(it)
                status.postValue(StatusModel("Ok"))
            }
            .onFailure { status.postValue(StatusModel(it)) }
    }

    override fun showAd(types: List<String>) {
        if (types.isEmpty())
            adEvent.postValue(AdType.USUAL)
        else
            showAd(types.first())
    }

    override fun showAd(type: String) {
        when (type) {
            "video" -> adEvent.postValue(AdType.VIDEO)
            "simple" -> adEvent.postValue(AdType.USUAL)
            else -> adEvent.postValue(AdType.USUAL)
        }
    }


}
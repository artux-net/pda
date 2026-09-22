package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.datatransport.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.chat.UserMessage
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.mapper.StageMapper
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.ChapterModel
import net.artux.pda.model.quest.NotificationModel
import net.artux.pda.model.quest.Stage
import net.artux.pda.model.quest.StageModel
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.TransferModel
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.CommandController
import net.artux.pda.repositories.MissionController
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.SellerRepository
import net.artux.pda.repositories.SummaryRepository
import net.artux.pda.repositories.UserRepository
import net.artux.pda.ui.viewmodels.event.OpenStageEvent
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import net.artux.pda.utils.AdType
import timber.log.Timber
import kotlin.random.Random

@HiltViewModel
class QuestViewModel @javax.inject.Inject constructor(
    var sellerRepository: SellerRepository,
    var summaryRepository: SummaryRepository,
    var userRepository: UserRepository,
    var repository: QuestRepository,
    var stageMapper: StageMapper,
    var mapper: StoryMapper,
    var missionController: MissionController,
    var commandController: CommandController,
    var statusMapper: StatusMapper
) : ViewModel() {

    var title: MutableLiveData<String> = MutableLiveData()
    var loadingState: MutableLiveData<Boolean> = MutableLiveData()
    var stage: MutableLiveData<StageModel> = MutableLiveData()
    var background: MutableLiveData<String> = MutableLiveData()

    var chapter: MutableLiveData<ChapterModel> = MutableLiveData()
    var map: MutableLiveData<GameMap> = MutableLiveData()
    var data: MutableLiveData<Map<String, String>> = MutableLiveData()

    val status: SingleLiveEvent<StatusModel> get() = commandController.status
    val storyData: MutableLiveData<StoryDataModel> get() = commandController.storyData
    val notification: SingleLiveEvent<NotificationModel> get() = commandController.notification

    var currentStoryId: Int = repository.getCurrentStoryId()
    var currentChapterId: Int = repository.getCurrentChapterId()
    var currentStageId: Long = -1
    var transferDisabled = true

    // commandController is a singleton observed via observeForever (not tied to any
    // LifecycleOwner), so this observer must be removed in onCleared() below - otherwise
    // every past QuestViewModel instance stays registered forever, leaking them and making
    // every future stageEvent fire beginWithStage() once per leaked instance.
    private val stageEventObserver = Observer<Event<OpenStageEvent>> {
        val stageId = it.payload.stageId
        var toSync = false
        val chapterId = if (it.payload.chapterId > -1) {
            toSync = true
            it.payload.chapterId
        } else
            currentChapterId
        beginWithStage(currentStoryId, chapterId, stageId, toSync)
    }

    init {
        //подписка на команду открытия стадии
        commandController.stageEvent.observeForever(stageEventObserver)
    }

    override fun onCleared() {
        super.onCleared()
        commandController.stageEvent.removeObserver(stageEventObserver)
    }

    fun updateStoryDataFromCache() {
        repository.getCachedStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
            .onFailure { status.postValue(StatusModel(it)) }
    }

     fun updateStoryDataFromServer() {
         viewModelScope.launch {
             repository.getStoryData()
                 .map { mapper.dataModel(it) }
                 .onSuccess { storyData.postValue(it) }
                 .onFailure { status.postValue(StatusModel(it)) }
         }
    }

    fun getCurrentStory(): StoryModel {
        return repository.getCachedStory(currentStoryId)
            .map { mapper.story(it) }
            .getOrThrow()
    }

    private suspend fun suspendUpdateData() {
        storyData.value = repository.getStoryData()
            .map { mapper.dataModel(it) }
            .getOrThrow()
        sellerRepository.getItems().getOrThrow()
    }

    fun beginWithStage(
        storyId: Int = currentStoryId,
        chapterId: Int,
        stageId: Long,
        sync: Boolean = true
    ) {
        if (storyId < 0) {
            exitStory()
            return
        }
        viewModelScope.launch {
            currentStoryId = storyId
            currentChapterId = chapterId

            loadingState.postValue(true)

            suspendUpdateData()

            repository.getChapter(storyId, chapterId)
                .map { mapper.chapter(it) }
                .onSuccess {
                    chapter.postValue(it)
                    val chapterStage = it.getStage(stageId)

                    if (chapterStage == null) {
                        status.postValue(StatusModel(Exception("Can not find stage with id: $stageId in chapter: $currentChapterId")))
                        return@launch
                    }
                    // it.stages is keyed by stage id (see StoryMapper.stagesMap), not by
                    // position, so stages[0] was looking up "the stage whose id is 0" rather
                    // than "the first stage" - almost always absent, silently posting null
                    // into this non-null LiveData. Use the actual resolved target stage.
                    chapterStage.background?.let(background::postValue)
                    if (sync) {
                        prepareSync(chapterStage)
                        syncNow()
                    }
                    setStage(chapterStage)
                }
                .onFailure {
                    status.postValue(StatusModel(it))
                }
            repository.getCachedStory(storyId)
                .map {
                    mapper.story(it)
                }.onSuccess {
                    missionController.missions = it.missions
                }
            loadingState.postValue(false)
        }
    }

    private fun prepareSync(chapterStage: Stage) {
        commandController.checkStage(currentStoryId, currentChapterId, chapterStage.id)
        commandController.process(chapterStage.actions)

        val texts = chapterStage.texts
        if (texts.isNotEmpty() && texts[0].text.isNotBlank())
            summaryRepository.check(
                UserMessage(
                    chapterStage.title ?: "",
                    chapterStage.texts[0].text,
                    chapterStage.background
                )
            )
    }

    // Suspend rather than launching its own coroutine: beginWithStage() calls this as the
    // last step of its own coroutine and previously relied on it finishing synchronously to
    // sequence loadingState correctly. A nested, un-awaited viewModelScope.launch here let
    // beginWithStage() post loadingState=false while this was still mid-flight (e.g. still
    // fetching a map or syncing), so the loading indicator could disappear before the stage
    // was actually ready. Callers that aren't already inside a coroutine (chooseTransfer)
    // wrap this call in their own viewModelScope.launch instead.
    private suspend fun setStage(chapterStage: Stage) {
        currentStageId = chapterStage.id
        Timber.i("Opening stage: ${chapterStage.id}")
        when (chapterStage.typeStage) {
            4 -> {
                //переход на карту
                val stageData = chapterStage.data ?: return
                syncNow()
                title.postValue("Loading map...")
                val mapId: String? = stageData["map"]
                if (mapId != null) {
                    repository.getMap(currentStoryId, mapId.toInt())
                        .map { mapper.map(it) }
                        .onSuccess {
                            stageData["pos"]?.let { pos -> it.defPos = pos }
                            Timber.i("${storyData.value}")
                            if (Random.nextFloat() < 0.1f) {
                                commandController.showAd(AdType.TRANSFER_VIDEO)
                            }
                            map.postValue(it)
                        }
                        .onFailure { status.postValue(StatusModel(it)) }
                } else
                    status.postValue(StatusModel("Указан тип стадии - карта, но id не задан"))
            }

            5, 6 -> {
                processData(chapterStage.data)
            }

            else -> {
                chapterStage.background?.let(background::postValue)
                if (storyData.value == null) {
                    status.postValue(StatusModel(Exception("Story Data null")))
                    return
                }
                if (chapterStage.isNeedSync())
                    syncNow()
                notification.postValue(stageMapper.notification(chapterStage, storyData.value))
                stage.postValue(stageMapper.model(chapterStage, storyData.value))
            }
        }
        transferDisabled = false
    }

    fun chooseTransfer(transfer: TransferModel) {
        if (transferDisabled)
            return
        val storyDataValue = storyData.value
        val chapterValue = chapter.value
        if (storyDataValue == null || chapterValue == null) {
            status.postValue(StatusModel(Exception("Не удалось выбрать переход: данные главы ещё не загружены")))
            return
        }
        transferDisabled = true
        summaryRepository.check(UserMessage(storyDataValue, transfer.text))

        val chapterStage = chapterValue.getStage(transfer.stageId)
        if (chapterStage != null) {
            viewModelScope.launch {
                prepareSync(chapterStage)
                setStage(chapterStage)
            }
        } else
            status.postValue(StatusModel(Exception("Не удалось найти стадию ${transfer.stageId} в главе: $currentChapterId")))
    }

    fun getCurrentStage(): Stage? {
        val chapterValue = chapter.value ?: return null
        val stageValue = stage.value ?: return null
        return chapterValue.getStage(stageValue.id)
    }

    private suspend fun syncNow() {
        title.postValue("Синхронизация")
        loadingState.postValue(true)

        commandController.syncNow()
            .onSuccess {
                summaryRepository.updateSummary()//save summary
            }.onFailure {
                status.postValue(StatusModel(it))
            }
        loadingState.postValue(false)
    }

    fun exitStory() {
        commandController.processWithServer(mapOf(Pair("exitStory", listOf())))
    }

    fun resetData() {
        viewModelScope.launch {
            userRepository.clearMemberCache()
            repository.clearCache()
            userRepository.getMember()
            commandController.resetData()
        }
    }

    fun clear() {
        repository.clearCache()
    }

    fun processDataWithActions(data: Map<String, String>?, actions: Map<String, MutableList<String>>) {
        loadingState.postValue(true)
        viewModelScope.launch {
            commandController.syncNow(actions)
                .onSuccess {
                    processData(data)
                }.onFailure {
                    status.postValue(StatusModel(it))
                }
            loadingState.postValue(false)
        }
    }

    private fun processData(data: Map<String, String>?) {
        if (data == null)
            return
        loadingState.postValue(true)
        Timber.i("Processing data - commands: ${data}")
        if (data.containsKey("chapter")) {
            val chapterId: String? = data["chapter"]
            val stageId: String? = data["stage"]
            if (!chapterId.isNullOrBlank() && !stageId.isNullOrBlank())
                beginWithStage(chapterId = chapterId.toInt(), stageId = stageId.toLong(), sync = false)

        } else if (data.containsKey("seller")) {
            val sellerId: String? = data["seller"]
            if (!sellerId.isNullOrBlank())
                this.data.postValue(data)
        }
    }

}
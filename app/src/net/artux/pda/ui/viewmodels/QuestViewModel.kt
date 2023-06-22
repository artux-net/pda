package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
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
import net.artux.pda.model.quest.*
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.*
import net.artux.pda.ui.viewmodels.event.ScreenDestination
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import timber.log.Timber

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
    var notification: MutableLiveData<NotificationModel> = MutableLiveData()
    var background: MutableLiveData<String> = MutableLiveData()

    var chapter: MutableLiveData<ChapterModel> = MutableLiveData()
    var map: MutableLiveData<GameMap> = MutableLiveData()
    var data: MutableLiveData<Map<String, String>> = MutableLiveData()

    val storyData: MutableLiveData<StoryDataModel> get() = commandController.storyData
    val status: SingleLiveEvent<StatusModel> get() = commandController.status

    var currentStoryId: Int = repository.getCurrentStoryId()
    var currentChapterId: Int = repository.getCurrentChapterId()
    var currentStageId: Long = -1
    var transferDisabled = true

    init {
        //подписка на команду открытия стадии
        commandController.stageEvent.observeForever {
            val stageId = it.payload.stageId
            var toSync = false
            val chapterId = if (it.payload.chapterId > -1) {
                toSync = true
                it.payload.chapterId
            } else
                currentChapterId
            beginWithStage(currentStoryId, chapterId, stageId, toSync)
        }
    }

    fun updateStoryDataFromCache() {
        repository.getCachedStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
            .onFailure { status.postValue(StatusModel(it)) }
    }

    fun getCurrentStory(): StoryModel {
        return repository.getCachedStory(currentStoryId)
            .map { mapper.story(it) }
            .getOrThrow()
    }

    private suspend fun suspendUpdateData() {
        storyData.value = repository.getStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
            .getOrThrow()
        sellerRepository.getItems().getOrThrow()
    }

    fun beginWithStage(
        storyId: Int = currentStoryId,
        chapterId: Int,
        stageId: Long,
        sync: Boolean = true
    ) {
        if (storyId == -1)
            throw Exception("Negative storyId")
        viewModelScope.launch {
            currentStoryId = storyId
            currentChapterId = chapterId

            loadingState.postValue(true)

            suspendUpdateData()

            repository.getChapter(storyId, chapterId)
                .map { mapper.chapter(it) }
                .onSuccess {
                    chapter.postValue(it)
                    background.postValue(it.stages[0]?.background)
                    val chapterStage = it.getStage(stageId)

                    if (chapterStage == null) {
                        status.postValue(StatusModel(Exception("Can not find stage with id: $stageId in chapter: $currentChapterId")))
                        return@launch
                    }
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
                    chapterStage.title,
                    chapterStage.texts[0].text,
                    chapterStage.background
                )
            )
    }

    private fun setStage(chapterStage: Stage) {
        currentStageId = chapterStage.id
        Timber.i("Opening stage: ${chapterStage.id}")
        viewModelScope.launch {
            when (chapterStage.typeStage) {
                4 -> {
                    //переход на карту
                    if (chapterStage.data == null)
                        return@launch
                    syncNow()
                    title.postValue("Loading map...")
                    val mapId: String? = chapterStage.data!!["map"]
                    if (mapId != null) {
                        if (chapterStage.data!!.containsKey("pos")) {
                            repository.getMap(currentStoryId, mapId.toInt())
                                .map { mapper.map(it) }
                                .onSuccess {
                                    if (chapterStage.data!!.containsKey("pos"))
                                        it.defPos = chapterStage.data!!["pos"].toString()
                                    Timber.i("${storyData.value}")
                                    map.postValue(it)
                                }
                                .onFailure { status.postValue(StatusModel(it)) }
                        }
                    } else
                        status.postValue(StatusModel("Указан тип стадии - карта, но id не задан"))
                }

                5, 6 -> {
                    processData(chapterStage.data)
                }

                else -> {
                    background.postValue(chapterStage.background)
                    if (storyData.value == null) {
                        status.postValue(StatusModel(Exception("Story Data null")))
                        return@launch
                    }
                    notification.postValue(stageMapper.notification(chapterStage, storyData.value))
                    stage.postValue(stageMapper.model(chapterStage, storyData.value))
                    transferDisabled = false
                }
            }
        }
    }

    fun chooseTransfer(transfer: TransferModel) {
        if (transferDisabled)
            return
        transferDisabled = true
        summaryRepository.check(UserMessage(storyData.value!!, transfer.text))

        val chapterStage = chapter.value!!.getStage(transfer.stageId)
        if (chapterStage != null) {
            prepareSync(chapterStage)
            setStage(chapterStage)
        } else
            status.postValue(StatusModel(Exception("Can not find stage with id: ${transfer.stageId} in chapter: $currentChapterId")))
    }

    fun getCurrentStage(): Stage? {
        return chapter.value!!.getStage(stage.value!!.id)
    }

    private suspend fun syncNow() {
        Timber.d("Start syncing story")
        title.postValue("Синхронизация")
        loadingState.postValue(true)

        commandController.syncNow()
            .onSuccess {
                summaryRepository.updateSummary()//save summary
                Timber.d("Successful sync, data: ${storyData.value}")
            }.onFailure {
                status.postValue(StatusModel(it))
            }
        loadingState.postValue(false)
    }

    fun syncNow(map: Map<String, MutableList<String>>) {
        viewModelScope.launch {
            commandController.syncNow(map)
        }
    }

    fun exitStory() {
        commandController.process(mapOf(Pair("exitStory", listOf())))
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

    fun processData(data: Map<String, String>?) {
        if (data == null)
            return
        loadingState.postValue(true)
        if (data.containsKey("chapter")) {
            val chapterId: String? = data["chapter"]
            val stageId: String? = data["stage"]
            if (chapterId != null && stageId != null) {
                beginWithStage(chapterId = chapterId.toInt(), stageId = stageId.toLong())
            }
        } else if (data.containsKey("seller")) {
            val sellerId: String? = data["seller"]
            if (sellerId != null) {
                this.data.postValue(data)
            }
        }

    }

}
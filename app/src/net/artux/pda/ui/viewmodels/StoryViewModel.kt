package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.map.model.input.GameMap
import net.artux.pda.model.StatusModel
import net.artux.pda.model.UserMessage
import net.artux.pda.model.mapper.StageMapper
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.*
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.SummaryRepository
import net.artux.pdanetwork.model.CommandBlock
import timber.log.Timber
import java.util.*

@HiltViewModel
class StoryViewModel @javax.inject.Inject constructor(
    var summaryRepository: SummaryRepository,
    var repository: QuestRepository,
    var stageMapper: StageMapper,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var title: MutableLiveData<String> = MutableLiveData()
    var loadingState: MutableLiveData<Boolean> = MutableLiveData()
    var stage: MutableLiveData<StageModel> = MutableLiveData()
    var notification: MutableLiveData<NotificationModel> = MutableLiveData()
    var background: MutableLiveData<String> = MutableLiveData()

    var stories: MutableLiveData<List<StoryItem>> = MutableLiveData()
    var chapter: MutableLiveData<ChapterModel> = MutableLiveData()
    var map: MutableLiveData<GameMap> = MutableLiveData()
    var data: MutableLiveData<Map<String, String>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    var summaryMessages: LinkedList<UserMessage> = LinkedList()
    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()


    var actionsMap: LinkedHashMap<String, MutableList<String>> = LinkedHashMap()

    var currentStoryId: Int = -1
    var currentChapterId: Int = -1

    fun updateStories() {
        viewModelScope.launch {
            repository.updateStories()
                .map { mapper.stories(it) }
                .onSuccess { stories.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun updateData(): MutableLiveData<StoryDataModel>{
        storyData = MutableLiveData()
        viewModelScope.launch {
            suspendUpdateData()
        }
        return storyData
    }

    private suspend fun suspendUpdateData() {
        repository.getStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
            .getOrThrow()
    }

    fun beginWithStage(chapterId: Int, stageId: Int) {
        if (currentStoryId == -1)
            throw Exception()
        beginWithStage(currentStoryId, chapterId, stageId, true)
    }

    fun beginWithStage(storyId: Int, chapterId: Int, stageId: Int, sync: Boolean) {
        viewModelScope.launch {
            currentStoryId = storyId
            currentChapterId = chapterId

            loadingState.postValue(true)

            suspendUpdateData()

            repository.getChapter(storyId, chapterId)
                .map { mapper.chapter(it) }
                .onSuccess {
                    chapter.postValue(it)
                    background.postValue(it.stages[0].backgroundUrl)
                    val chapterStage = it.getStage(stageId)

                    if (chapterStage != null) {
                        if (sync) {
                            prepareSync(chapterStage)
                            sync()
                        }
                        setStage(chapterStage)
                    } else
                        status.postValue(StatusModel(Exception("Can not find stage with id: $stageId in chapter: $currentChapterId")))
                }
                .onFailure { status.postValue(StatusModel(it)) }
            loadingState.postValue(false)
        }
    }

    private fun prepareSync(chapterStage: Stage) {
        var states = actionsMap["state"]
        if (states == null)
            states = LinkedList()
        states.add("$currentStoryId:$currentChapterId:${chapterStage.id}")
        actionsMap["state"] = states

        if (chapterStage.typeStage == 7 && chapterStage.texts[0].text != null)
            summaryMessages.add(
                UserMessage(
                    chapterStage.title,
                    chapterStage.texts[0].text,
                    chapterStage.backgroundUrl
                )
            )
    }

    private fun setStage(chapterStage: Stage) {
        Timber.i("Opening stage: $chapterStage")
        viewModelScope.launch {
            when (chapterStage.typeStage) {
                4 -> {
                    //переход на карту
                    sync()
                    title.postValue("Загрузка карты..")
                    val mapId: String? = chapterStage.data["map"]
                    if (mapId != null) {
                        if (chapterStage.data.containsKey("pos")) {
                            repository.getMap(currentStoryId, mapId.toInt())
                                .map { mapper.map(it) }
                                .onSuccess {
                                    if (chapterStage.data.containsKey("pos"))
                                        it.setPlayerPos(chapterStage.data["pos"])
                                    Timber.i("${storyData.value}")
                                    map.postValue(it)
                                }
                                .onFailure { status.postValue(StatusModel(it)) }
                        }
                    } else
                        status.postValue(StatusModel("Указан тип стадии - карта, но не id не задан"))
                }
                5 -> {
                    //переход в другую главу
                    val chapter = chapterStage.data["chapter"]
                    val stage = chapterStage.data["stage"]
                    if (chapter != null && stage != null) {
                        val chapterId: Int = chapter.toInt()
                        val stageId: Int = stage.toInt()
                        beginWithStage(chapterId, stageId)
                    }
                }
                6 -> {
                    // data - действие
                    loadingState.postValue(true)
                    data.postValue(chapterStage.data)
                }
                else -> {
                    background.postValue(chapterStage.backgroundUrl)
                    if (storyData.value != null) {
                        notification.postValue(
                            stageMapper.notification(
                                chapterStage,
                                storyData.value
                            )
                        )
                        stage.postValue(stageMapper.model(chapterStage, storyData.value))
                    }
                }
            }
        }


    }

    fun chooseTransfer(transfer: TransferModel) {
        viewModelScope.launch {
            if (stage.value!!.type == StageType.DIALOG) {
                summaryMessages.add(UserMessage(storyData.value, transfer.text))
            }

            val chapterStage = chapter.value!!.getStage(transfer.stageId)
            if (chapterStage != null) {
                prepareSync(chapterStage)
                setStage(chapterStage)
            } else
                status.postValue(StatusModel(Exception("Can not find stage with id: ${transfer.stageId} in chapter: $currentChapterId")))
        }
    }

    private suspend fun sync() {
        Timber.d("Start syncing story")
        title.postValue("Синхронизация")
        loadingState.postValue(true)
        Timber.d("Actions map: $actionsMap")
        repository.syncMember(CommandBlock().actions(actionsMap))
            .map { mapper.dataModel(it) }
            .onSuccess {
                storyData.postValue(it)
                summaryRepository.updateSummary(summaryMessages)//save summary

                actionsMap = LinkedHashMap()// reset sync map
                summaryMessages = LinkedList()//reset summary
                loadingState.postValue(false)
                Timber.d("Successful sync, data: ${storyData.value}")
            }.onFailure {
                loadingState.postValue(false)
                status.postValue(StatusModel(it))
            }
            .getOrNull()
    }

    fun exitStory() {
        viewModelScope.launch {
            sync()
            val actions = mapOf(Pair("reset_current", listOf("")))
            repository.syncMember(CommandBlock().actions(actions))
                .onSuccess { data.postValue(mapOf(Pair("exit", ""))) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.clearCache()
            repository.resetData()
                .map { mapper.dataModel(it) }
                .onSuccess {
                    storyData.postValue(it)
                    status.postValue(StatusModel("Ok"))
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun clear() {
        repository.clearCache()
    }

    fun updateDataFromCache() {
        repository.getCachedStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
    }

}
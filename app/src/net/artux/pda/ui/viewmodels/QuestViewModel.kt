package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.chat.UserMessage
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.mapper.ItemMapper
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
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.SellerRepository
import net.artux.pda.repositories.SummaryRepository
import net.artux.pda.repositories.UserRepository
import net.artux.pdanetwork.model.CommandBlock
import timber.log.Timber
import java.util.LinkedList

@HiltViewModel
class QuestViewModel @javax.inject.Inject constructor(
    var sellerRepository: SellerRepository,
    var summaryRepository: SummaryRepository,
    var userRepository: UserRepository,
    var repository: QuestRepository,
    var stageMapper: StageMapper,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    val itemMapper: ItemMapper = ItemMapper.INSTANCE

    var title: MutableLiveData<String> = MutableLiveData()
    var loadingState: MutableLiveData<Boolean> = MutableLiveData()
    var stage: MutableLiveData<StageModel> = MutableLiveData()
    var notification: MutableLiveData<NotificationModel> = MutableLiveData()
    var background: MutableLiveData<String> = MutableLiveData()

    var chapter: MutableLiveData<ChapterModel> = MutableLiveData()
    var map: MutableLiveData<GameMap> = MutableLiveData()
    var data: MutableLiveData<Map<String, String>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    var summaryMessages: LinkedList<UserMessage> = LinkedList()
    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()

    var actionsMap: LinkedHashMap<String, MutableList<String>> = LinkedHashMap()

    var currentStoryId: Int = -1
    var currentChapterId: Int = -1

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
                    background.postValue(it.stages[0].background)
                    val chapterStage = it.getStage(stageId)

                    if (chapterStage != null) {
                        if (sync) {
                            prepareSync(chapterStage)
                            syncNow()
                        }
                        setStage(chapterStage)
                    } else
                        status.postValue(StatusModel(Exception("Can not find stage with id: $stageId in chapter: $currentChapterId")))
                }
                .onFailure {
                    status.postValue(StatusModel(it))
                }
            loadingState.postValue(false)
        }
    }

    private fun prepareSync(chapterStage: Stage) {
        if (chapterStage.actions != null)
            processLocalActions(chapterStage.actions!!)

        var states = actionsMap["state"]
        if (states == null)
            states = LinkedList()
        states.add("$currentStoryId:$currentChapterId:${chapterStage.id}")
        actionsMap["state"] = states

        if (chapterStage.texts != null
            && chapterStage.texts!!.isNotEmpty()
            && chapterStage.texts!![0].text.isNotBlank()
        )
            summaryMessages.add(
                UserMessage(
                    chapterStage.title,
                    chapterStage.texts!![0].text,
                    chapterStage.background
                )
            )
    }

    private fun setStage(chapterStage: Stage) {
        Timber.i("Opening stage: $chapterStage")
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
                    notification.postValue(
                        stageMapper.notification(chapterStage, storyData.value)
                    )

                    val stageModel = stageMapper.model(chapterStage, storyData.value)
                    stage.postValue(stageModel)
                }
            }
        }
    }

    fun chooseTransfer(transfer: TransferModel) {
        viewModelScope.launch {
            summaryMessages.add(
                UserMessage(
                    storyData.value!!,
                    transfer.text
                )
            )

            val chapterStage = chapter.value!!.getStage(transfer.stageId)
            if (chapterStage != null) {
                prepareSync(chapterStage)
                setStage(chapterStage)
                if (chapterStage.actions != null && chapterStage.actions!!.containsKey("syncNow"))
                    syncNow()
            } else
                status.postValue(StatusModel(Exception("Can not find stage with id: ${transfer.stageId} in chapter: $currentChapterId")))
        }
    }

    fun getCurrentStage(): Stage? {
        return chapter.value!!.getStage(stage.value!!.id)
    }

    private suspend fun syncNow() {
        Timber.d("Start syncing story")
        title.postValue("Синхронизация")
        loadingState.postValue(true)

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

    fun syncNow(map: Map<String, MutableList<String>>) {
        actionsMap.putAll(map)
        viewModelScope.launch {
            syncNow()
        }
    }

    fun openMap(mapId: Int) {

    }

    fun exitStory() {
        viewModelScope.launch {
            syncNow()
            val actions = mapOf(Pair("exitStory", listOf("")))
            repository.syncMember(CommandBlock().actions(actions))
                .onSuccess { data.postValue(mapOf(Pair("exit", ""))) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            userRepository.clearMemberCache()
            repository.clearCache()
            userRepository.getMember()
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

    private fun processLocalActions(actions: HashMap<String, List<String>>) {
        if (actions.containsKey("finishStory")) {
            actionsMap["finishStory"] = mutableListOf("")
            exitStory()
        } else if (actions.containsKey("openStage")) {
            val list: List<String> = actions["openStage"] ?: return
            var chapterId = currentChapterId
            val stageId: Int
            if (list.size > 2)
                return
            else if (list.size == 1)
                stageId = list[0].toInt()
            else {
                chapterId = list[0].toInt()
                stageId = list[1].toInt()
            }
            beginWithStage(chapterId, stageId)

        } else if (actions.containsKey("openSeller")) {
            val sellerId: Int? = actions["openSeller"]?.get(0)?.toInt()
            if (sellerId != null) {
                this.data.postValue(mapOf(Pair("seller", "$sellerId")))
            }
        }
    }

    fun processData(data: Map<String, String>?) {
        if (data == null)
            return
        loadingState.postValue(true)
        if (data.containsKey("over")) {
            actionsMap["over"] = mutableListOf("")
            exitStory()
        } else if (data.containsKey("chapter")) {
            val chapterId: String? = data["chapter"]
            val stageId: String? = data["stage"]
            if (chapterId != null && stageId != null) {
                beginWithStage(chapterId.toInt(), stageId.toInt())
            }
        } else if (data.containsKey("seller")) {
            val sellerId: String? = data["seller"]
            if (sellerId != null) {
                this.data.postValue(data)
            }
        }

    }

}
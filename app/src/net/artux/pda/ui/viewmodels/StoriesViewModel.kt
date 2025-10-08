package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.common.PropertyFields
import net.artux.pda.model.StatusModel
import net.artux.pda.model.StoryAlert
import net.artux.pda.model.StorySelector
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.StoryItem
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.model.quest.story.StoryStateModel
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.UserRepository
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import net.artux.pdanetwork.model.CommandBlock
import java.util.Properties

@HiltViewModel
class StoriesViewModel @javax.inject.Inject constructor(
    var userRepository: UserRepository,
    var properties: Properties,
    var repository: QuestRepository,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var selectedStoryId = -1
    var stories: MutableLiveData<List<StoryItem>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()
    var storyState: MutableLiveData<StoryStateModel> = MutableLiveData()
    var storyAlert: SingleLiveEvent<StoryAlert> = SingleLiveEvent()
    var storySelector: MutableLiveData<StorySelector> = MutableLiveData()

    private fun isTester() = properties[PropertyFields.TESTER_MODE] == true

    init {
        //updateData().
    }

    fun updateStories() =
        viewModelScope.launch {
            repository.updateStories()
                .map { mapper.storyItem(it, storyData.value) }
                .onSuccess {
                    if (isTester())
                        it.add(
                            StoryItem(
                                -1,
                                "Загрузка стадии на выбор",
                                null,
                                "Используется для тестирования, ввод формата история:глава:номер стадии"
                            )
                        )
                    stories.postValue(it)
                }
                .onFailure {
                    status.postValue(StatusModel(it))
                }
        }

    fun updateData() =
        viewModelScope.launch {
            repository.getStoryData()
                .map { mapper.dataModel(it) }
                .onSuccess { storyData.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }

    fun resetSingleStory(id: Int) {
        viewModelScope.launch {
            val block = CommandBlock()
                .actions(mapOf(Pair("reset", listOf("$id"))))
            repository.syncMember(block)
                .map { mapper.dataModel(it) }
                .onSuccess {
                    storyData.postValue(it)
                    status.postValue(StatusModel("Ok"))
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun selectStory(storyItem: StoryItem) {
        val storyId = storyItem.id
        selectedStoryId = storyId
        var chapterId = 1
        var stageId = 0
        var current = false

        val data = storyData.value ?: return
        for (id in storyItem.needs) {
            data.storyStates.forEach {
                if (it.storyId == id && !it.over) {
                    storyAlert.postValue(StoryAlert.OTHER_NOT_COMPLETE)
                    return
                }
            }
        }

        val state = data.getStateByStoryId(storyId)?.apply {
            chapterId = this.chapterId
            stageId = this.stageId
            current = this.current
        }

        if (state?.over == true) {
            storyAlert.postValue(StoryAlert.ALREADY_COMPLETE)
            return
        }

        storySelector.postValue(StorySelector(storyId, chapterId, stageId, current))
    }

    fun selectStory(storyId: Int, chapterId: Int, stageId: Int, isCurrent: Boolean = false) {
        selectedStoryId = storyId
        storySelector.postValue(StorySelector(storyId, chapterId, stageId, isCurrent))
    }

    fun clear() {
        repository.clearCache()
    }

}
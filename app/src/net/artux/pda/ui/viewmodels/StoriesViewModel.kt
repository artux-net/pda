package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.common.PropertyFields
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.StoryItem
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.UserRepository
import java.util.*

@HiltViewModel
class StoriesViewModel @javax.inject.Inject constructor(
    var userRepository: UserRepository,
    var properties: Properties,
    var repository: QuestRepository,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var stories: MutableLiveData<List<StoryItem>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()


    var currentStoryId: Int = -1
    var currentChapterId: Int = -1

    fun updateStories() {
        viewModelScope.launch {
            repository.updateStories()
                .map { mapper.stories(it) }
                .onSuccess {
                    if (properties.getProperty(
                            PropertyFields.TESTER_MODE,
                            "false"
                        ).equals(true.toString())
                    ) {
                        val item = StoryItem()
                        item.id = -1
                        item.title = "Загрузка стадии на выбор"
                        item.desc = ".."
                        it.add(item)
                    }
                    stories.postValue(it)
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun getCurrentStory(): StoryModel {
        return repository.getCachedStory(currentStoryId)
            .map { mapper.story(it) }
            .getOrThrow()
    }

    fun updateData(): MutableLiveData<StoryDataModel> {
        storyData = MutableLiveData()
        viewModelScope.launch {
            suspendUpdateData()
        }
        return storyData
    }

    private suspend fun suspendUpdateData() {
        storyData.value = repository.getStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
            .getOrThrow()
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

    fun updateDataFromCache() {
        repository.getCachedStoryData()
            .map { mapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
    }

}
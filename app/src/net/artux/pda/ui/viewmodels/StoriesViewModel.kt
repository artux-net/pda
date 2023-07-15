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

    var stories: MutableLiveData<List<StoryItem>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()

    fun updateStories() =
        viewModelScope.launch {
            repository.updateStories()
                .map { mapper.storyItem(it) }
                .onSuccess {
                    if (properties[PropertyFields.TESTER_MODE] == true)
                        it.add(StoryItem(
                            -1,
                            "Загрузка стадии на выбор",
                            null,
                            "Используется для тестирования, ввод формата история:глава:номер стадии"))
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

    fun clear() {
        repository.clearCache()
    }

}
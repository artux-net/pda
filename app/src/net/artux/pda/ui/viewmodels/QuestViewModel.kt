package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.map.model.input.Map
import net.artux.pda.model.StatusModel
import net.artux.pda.model.items.WearableModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.Chapter
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.QuestRepository
import net.artux.pdanetwork.model.CommandBlock
import javax.inject.Inject

@HiltViewModel
class QuestViewModel @Inject constructor(
    var repository: QuestRepository,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var chapter: MutableLiveData<Chapter> = MutableLiveData()
    var map: MutableLiveData<Map> = MutableLiveData()
    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()


    fun updateDataFromCache() {
        repository.getCachedStoryData()
            .onSuccess { storyData.postValue(mapper.dataModel(it)) }
    }

    fun getCachedData(): StoryDataModel? {
        return repository.getCachedStoryData()
            .map { mapper.dataModel(it) }
            .getOrNull()
    }


    private fun getCachedChapter(storyId: Int, chapterId: Int): Chapter? {
        return repository.getCachedChapter(storyId, chapterId).getOrNull()
    }

    private fun getCachedMap(storyId: Int, chapterId: Int): Map? {
        return repository.getCachedMap(storyId, chapterId).getOrNull()
    }

    fun applyActions(map: HashMap<String, List<String>>) {
        viewModelScope.launch {
            repository.syncMember(CommandBlock().actions(map)).onSuccess {
                storyData.postValue(mapper.dataModel(it))
            }
        }
    }


    fun updateChapter(storyId: Int, chapterId: Int) {
        viewModelScope.launch {
            repository.getChapter(storyId, chapterId)
                .onSuccess { chapter.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun updateMap(storyId: Int, mapId: Int) {
        viewModelScope.launch {
            repository.getMap(storyId, mapId)
                .onSuccess { map.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }

        }
    }

    fun getChapter(storyId: Int, chapterId: Int): MutableLiveData<Chapter> {
        chapter.value = getCachedChapter(storyId, chapterId)
        updateChapter(storyId, chapterId)
        return chapter
    }

    fun getMap(storyId: Int, mapId: Int): MutableLiveData<Map> {
        map.value = getCachedMap(storyId, mapId)
        updateMap(storyId, mapId)
        return map
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetData()
                .onSuccess {
                    storyData.postValue(mapper.dataModel(it))
                    status.postValue(StatusModel("Ok!"))
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun clear() {
        repository.clearCache()
    }

    fun updateData() {
        viewModelScope.launch {
            repository.getStoryData()
                .onSuccess { storyData.postValue(mapper.dataModel(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun setWearable(wearable: WearableModel) {
        viewModelScope.launch {
            repository.setWearableItem(wearable.id, wearable.type.toString())
                .onSuccess { status.postValue(statusMapper.model(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }
}
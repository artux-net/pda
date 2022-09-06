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
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.util.Result
import net.artux.pda.ui.fragments.quest.models.Chapter
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


    fun getCachedData(): MutableLiveData<StoryDataModel>{
        storyData.value = repository.getCachedStoryData()
            .map { mapper.dataModel(it) }
            .getOrNull()
        return storyData
    }

    private fun getCachedChapter(storyId: Int, chapterId: Int): Chapter? {
        val response = repository.getCachedChapter(storyId, chapterId)
        return if (response is Result.Success)
            response.data
        else null
    }

    private fun getCachedMap(storyId: Int, chapterId: Int): Map? {
        val response = repository.getCachedMap(storyId, chapterId)
        return if (response is Result.Success)
            response.data
        else null
    }

    fun applyActions(map: HashMap<String, List<String>>) {
        viewModelScope.launch {
            val result = repository.syncMember(CommandBlock().actions(map))
            if (result is Result.Success) {
                storyData.postValue(result.map { mapper.dataModel(it) }.getOrThrow())
            }
        }
    }


    fun updateChapter(storyId: Int, chapterId: Int) {
        viewModelScope.launch {
            val response = repository.getChapter(storyId, chapterId)
            if (response is Result.Success)
                chapter.postValue(response.data)
            else if (response is Result.Error)
                status.postValue(StatusModel(response.exception))
        }
    }

    fun updateMap(storyId: Int, mapId: Int) {
        viewModelScope.launch {
            val response = repository.getMap(storyId, mapId)
            if (response is Result.Success)
                map.postValue(response.data)
            else if (response is Result.Error)
                status.postValue(StatusModel(response.exception))
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
            storyData.postValue(repository.resetData().map { mapper.dataModel(it) }.getOrThrow())
        }
    }

    fun clear() {
        repository.clearCache()
    }

    fun updateData() {
        viewModelScope.launch {
            storyData.postValue(repository.getStoryData().map { mapper.dataModel(it) }.getOrThrow())
        }
    }

    fun setWearable(wearable: WearableModel) {
        viewModelScope.launch {
            val result = repository.setWearableItem(wearable.id, wearable.type.toString())

            if (result is Result.Success)
                status.postValue(statusMapper.model(result.data))
            else if (result is Result.Error)
                status.postValue(StatusModel(result.exception))
        }
    }
}
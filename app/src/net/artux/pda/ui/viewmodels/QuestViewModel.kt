package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.artux.pda.map.model.input.Map
import net.artux.pda.models.quest.story.StoryDataModel
import net.artux.pda.models.quest.story.StoryMapper
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.util.Result
import net.artux.pda.ui.fragments.quest.models.Chapter
import javax.inject.Inject

class QuestViewModel @Inject constructor(
    var repository: QuestRepository,
    var mapper: StoryMapper
) : ViewModel() {
    var chapter: MutableLiveData<Chapter> = MutableLiveData()
    var map: MutableLiveData<Map> = MutableLiveData()

    var storyData: MutableLiveData<Result<StoryDataModel>> =
        MutableLiveData(repository.getCachedStoryData().map { mapper.dataModel(it) })

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
            storyData.postValue(repository.syncMember(map).map { mapper.dataModel(it) })
        }
    }


    fun updateChapter(storyId: Int, chapterId: Int) {
        viewModelScope.launch {
            val response = repository.getChapter(storyId, chapterId)
            if (response is Result.Success)
                chapter.postValue(response.data)
        }
    }

    fun updateMap(storyId: Int, mapId: Int) {
        viewModelScope.launch {
            val response = repository.getMap(storyId, mapId)
            if (response is Result.Success)
                map.postValue(response.data)
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
            storyData.postValue(repository.resetData().map { mapper.dataModel(it) })
        }
    }

    fun clear() {
        repository.clearCache()
    }

    fun updateData() {
        viewModelScope.launch {
            storyData.postValue(repository.getStoryData().map { mapper.dataModel(it) })
        }
    }
}
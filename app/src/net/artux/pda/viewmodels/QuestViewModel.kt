package net.artux.pda.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.artux.pda.map.model.Map
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.repositories.Result
import net.artux.pda.ui.fragments.quest.models.Chapter
import javax.inject.Inject

class QuestViewModel @Inject constructor(
    var repository: QuestRepository
) : ViewModel() {
    var chapter: MutableLiveData<Chapter> = MutableLiveData()
    var map: MutableLiveData<Map> = MutableLiveData()

    private fun getCachedChapter(storyId: Int, chapterId: Int) : Chapter? {
        val response = repository.getCachedChapter(storyId, chapterId)
        return if (response is Result.Success)
            response.data
        else null
    }

    private fun getCachedMap(storyId: Int, chapterId: Int) : Map? {
        val response = repository.getCachedMap(storyId, chapterId)
        return if (response is Result.Success)
            response.data
        else null
    }

    fun updateChapter(storyId: Int, chapterId: Int) {
        GlobalScope.launch {
            val response = repository.getChapter(storyId, chapterId)
            if (response is Result.Success)
                chapter.postValue(response.data)
        }
    }

    fun updateMap(storyId: Int, mapId: Int) {
        GlobalScope.launch {
            val response = repository.getMap(storyId, mapId)
            if (response is Result.Success)
                map.postValue(response.data)
        }
    }

    fun getChapter(storyId: Int, chapterId: Int) : MutableLiveData<Chapter> {
        chapter.value = getCachedChapter(storyId, chapterId)
        updateChapter(storyId, chapterId)
        return chapter
    }

    fun getMap(storyId: Int, mapId: Int) : MutableLiveData<Map> {
        map.value = getCachedMap(storyId, mapId)
        updateMap(storyId, mapId)
        return map
    }

}
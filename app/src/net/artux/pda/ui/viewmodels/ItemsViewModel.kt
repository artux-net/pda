package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.items.WearableModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    var repository: QuestRepository,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()
    var status: SingleLiveEvent<StatusModel> = SingleLiveEvent()

    fun setWearable(wearable: WearableModel) {
        viewModelScope.launch {
            repository.setWearableItem(wearable.id)
                .onSuccess { status.postValue(statusMapper.model(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
            updateData()
        }
    }

    fun updateDataFromCache() {
        viewModelScope.launch {
            repository.getCachedStoryData()
                .onSuccess {
                    storyData.postValue(mapper.dataModel(it))
                }
        }
    }

    fun updateData() {
        viewModelScope.launch {
            repository.getStoryData()
                .map { mapper.dataModel(it) }
                .onSuccess {
                    storyData.postValue(it)
                }
        }
    }
}
package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.map.model.input.GameMap
import net.artux.pda.model.StatusModel
import net.artux.pda.model.items.WearableModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.ChapterModel
import net.artux.pda.repositories.QuestRepository
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    var repository: QuestRepository,
    var mapper: StoryMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var chapter: MutableLiveData<ChapterModel> = MutableLiveData()
    var map: MutableLiveData<GameMap> = MutableLiveData()

    var status: MutableLiveData<StatusModel> = MutableLiveData()

    fun setWearable(wearable: WearableModel) {
        viewModelScope.launch {
            repository.setWearableItem(wearable.id, wearable.type.toString())
                .onSuccess { status.postValue(statusMapper.model(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }
}
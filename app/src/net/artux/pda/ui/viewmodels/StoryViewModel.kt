package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.artux.pda.map.model.input.Map
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.quest.*
import net.artux.pda.repositories.QuestRepository
import net.artux.pda.ui.fragments.quest.QuestController

@HiltViewModel
class StoryViewModel  @javax.inject.Inject constructor(
    var repository: QuestRepository,
var mapper: StoryMapper,
var statusMapper: StatusMapper
) : ViewModel(), QuestController {


    var stage: MutableLiveData<StageModel> = MutableLiveData()
    var chapter: MutableLiveData<Chapter> = MutableLiveData()
    var map: MutableLiveData<Map> = MutableLiveData()
    var storiesContainer: MutableLiveData<StoriesContainer> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    override fun beginWithStage(stageId: Int) {
        TODO("Not yet implemented")
    }

    override fun beginWithStage(stageId: Int, sync: Boolean) {
        TODO("Not yet implemented")
    }

    override fun chooseTransfer(transfer: TransferModel?) {
        TODO("Not yet implemented")
    }

    override fun getActualStage(): Stage {
        TODO("Not yet implemented")
    }

    override fun getStoryId(): Int {
        TODO("Not yet implemented")
    }

    override fun getChapterId(): Int {
        TODO("Not yet implemented")
    }
}
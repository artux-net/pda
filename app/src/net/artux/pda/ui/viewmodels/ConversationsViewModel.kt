package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.repositories.ConversationRepository
import net.artux.pdanetwork.model.ConversationDTO
import net.artux.pdanetwork.model.QueryPage
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    var repository: ConversationRepository,
) : ViewModel() {
    var conversations: MutableLiveData<List<ConversationDTO>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    fun update() {
        viewModelScope.launch {
            repository.getConversations(QueryPage().size(10))
                .onSuccess { it ->
                    conversations.postValue(it)
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }
}
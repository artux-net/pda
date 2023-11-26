package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.repositories.ConversationRepository
import net.artux.pdanetwork.model.ConversationCreateDTO
import net.artux.pdanetwork.model.ConversationDTO
import net.artux.pdanetwork.model.QueryPage
import java.util.UUID
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
                .onSuccess { conversations.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun save(conversation: ConversationCreateDTO) {
        viewModelScope.launch {
            repository.createConversation(conversation)
                .onSuccess { conversations.postValue(listOf(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun getPrivateByUser(userId: UUID) {
        viewModelScope.launch {
            repository.getPrivateConversation(userId)
                .onSuccess { conversations.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun delete(conversationId: UUID) {
        viewModelScope.launch {
            repository.deleteConversation(conversationId)
                .onSuccess { update() }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }
}
package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.CommentMapper
import net.artux.pda.model.news.CommentModel
import net.artux.pda.repositories.CommentsRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    var repository: CommentsRepository
) : ViewModel() {

    var comments: MutableLiveData<List<CommentModel>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var mapper: CommentMapper = CommentMapper.INSTANCE

    fun updateComments(typeComment: CommentsRepository.CommentType, id: UUID, page: Int) {
        viewModelScope.launch {
            repository.getComments(typeComment, id, page)
                .map { mapper.model(it.content) }
                .onSuccess { comments.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun leaveComment(typeComment: CommentsRepository.CommentType, id: UUID, content: String) {
        viewModelScope.launch {
            repository.leaveComment(typeComment, id, content)
                .onSuccess {
                    status.postValue(StatusModel("Опубликовано!"))
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun likeComment(uuid: UUID) {
        viewModelScope.launch {
            repository.likeComment(uuid)
                .onSuccess {
                    val comment = comments.value?.findLast { it.id == uuid }
                    val m = if (it){
                        comment?.likes = comment?.likes!!.plus(1)

                        "Оценка установлена"
                    } else{
                        comment?.likes = comment?.likes!!.minus(1)
                        "Оценка снята"
                    }
                    comments.postValue(comments.value)
                    status.postValue(StatusModel(m))
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }


}
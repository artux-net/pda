package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.app.DataManager
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.model.user.UserModel
import net.artux.pda.repositories.UserRepository
import net.artux.pda.repositories.util.Result
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private var userRepository: UserRepository,
    var dataManager: DataManager,
    var userMapper: UserMapper,
    var statusMapper: StatusMapper
) : ViewModel() {

    var member: MutableLiveData<UserModel> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var storyData: MutableLiveData<Result<StoryDataModel>> = MutableLiveData()

    fun updateFromCache() {
        member.postValue(userRepository.getCachedMember()
            .map { userMapper.model(it) }
            .getOrNull())
    }

    fun getFromCache(): UserModel? {
        return userRepository.getCachedMember()
            .map { userMapper.model(it) }
            .getOrNull()
    }

    fun updateMember() {
        viewModelScope.launch {
            userRepository.getMember()
                .onSuccess { member.postValue(userMapper.model(it)) }
                .onFailure {
                    it.printStackTrace()
                    status.postValue(StatusModel(it))
                }
        }
    }


    fun updateMemberWithReset() {
        viewModelScope.launch {
            userRepository.clearMemberCache()
            member.postValue(userRepository.getMember().map { userMapper.model(it) }.getOrNull())
        }
    }

    fun getId(): UUID {
        return userRepository.getCachedMember().getOrThrow().id!!
    }

    fun signOut() {
        dataManager.removeAllData()
        userRepository.clearMemberCache()
    }

    fun requestFriend(pdaId: UUID) {
        //todo
    }

}
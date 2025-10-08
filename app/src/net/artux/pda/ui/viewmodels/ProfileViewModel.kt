package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.model.user.ProfileModel
import net.artux.pda.model.user.SimpleUserModel
import net.artux.pda.model.user.UserRelation
import net.artux.pda.repositories.UserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    var userRepository: UserRepository,
    var userMapper: UserMapper
) : ViewModel() {
    var profile: MutableLiveData<ProfileModel> = MutableLiveData()
    var friends: MutableLiveData<List<SimpleUserModel>> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    private fun getCachedProfile(pdaId: UUID): Result<ProfileModel> {
        val response = userRepository.getCachedProfile(pdaId)
        return response.map { userMapper.model(it) }
    }

    fun updateProfile(uuid: UUID?) {
        if (uuid != null)
            viewModelScope.launch {
                userRepository.getProfile(uuid)
                    .map { userMapper.model(it) }
                    .onSuccess {
                        it.id = uuid
                        profile.postValue(it)
                    }
                    .onFailure { status.postValue(StatusModel(it)) }
            }
    }

    fun getId(): UUID {
        return userRepository.getCachedMember().getOrThrow().id!!
    }

    fun updateFriends(uuid: UUID, userRelation: UserRelation) {
        viewModelScope.launch {
            userRepository.getFriends(uuid, userRelation)
                .onSuccess { friends.postValue(userMapper.simple(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun updateMyRequests() {
        viewModelScope.launch {
            userRepository.getUserRequests()
                .onSuccess { friends.postValue(userMapper.simple(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }
}
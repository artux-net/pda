package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.model.user.ProfileModel
import net.artux.pda.repositories.UserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    var userRepository: UserRepository,
    var userMapper: UserMapper
) : ViewModel() {
    var profile: MutableLiveData<ProfileModel> = MutableLiveData()

    private fun getCachedProfile(pdaId: UUID): Result<ProfileModel> {
        val response = userRepository.getCachedProfile(pdaId)
        return response.map { userMapper.model(it) }
    }

    fun updateProfile(pdaId: UUID) {
        viewModelScope.launch {
            userRepository.getProfile(pdaId)
                .map { userMapper.model(it) }
                .onSuccess {
                    profile.postValue(it)
                }
        }
    }

    fun getId(): UUID {
        return userRepository.getCachedMember().getOrThrow().id!!
    }

}
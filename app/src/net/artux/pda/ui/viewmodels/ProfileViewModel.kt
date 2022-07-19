package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.artux.pda.generated.models.UserDto
import net.artux.pda.models.user.ProfileModel
import net.artux.pda.models.user.UserMapper
import net.artux.pda.repositories.UserRepository
import net.artux.pda.repositories.util.Result
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    var userRepository: UserRepository, var userMapper: UserMapper
) : ViewModel() {
    var profile: MutableLiveData<Result<ProfileModel>> = MutableLiveData(getCachedProfile(getId()))

    private fun getCachedProfile(pdaId: Int): Result<ProfileModel> {
        val response = userRepository.getCachedProfile(pdaId)
        return response.map { userMapper.model(it) }
    }

    fun updateProfile(pdaId: Int) {
        viewModelScope.launch {
            profile.postValue(userRepository.getProfile(pdaId.toLong()).map { userMapper.model(it) })
        }
    }

    fun getId(): Int {
        return userRepository.getCachedMember().getOrDefault(UserDto()).pdaId!!.toInt()
    }

}
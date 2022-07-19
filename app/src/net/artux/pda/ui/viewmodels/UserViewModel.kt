package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.artux.pda.generated.models.UserDto
import net.artux.pda.models.user.UserMapper
import net.artux.pda.models.user.UserModel
import net.artux.pda.repositories.UserRepository
import net.artux.pda.repositories.util.Result
import javax.inject.Inject

class UserViewModel @Inject constructor(
    private var userRepository: UserRepository, var userMapper: UserMapper
) : ViewModel() {

    var member: MutableLiveData<Result<UserModel>> =
        MutableLiveData(userRepository.getCachedMember().map { userMapper.dto(it) })



    fun updateMember() {
        viewModelScope.launch {
            member.postValue(userRepository.getMember().map { userMapper.dto(it) })
        }
    }


    fun updateMemberWithReset() {
        viewModelScope.launch {
            userRepository.clearMemberCache()
            member.postValue(userRepository.getMember().map { userMapper.dto(it) })
        }
    }

    fun getId(): Int {
        return userRepository.getCachedMember().getOrDefault(UserDto()).pdaId!!.toInt()
    }

    fun clearCache() {
        viewModelScope.launch {
            userRepository.clearMemberCache()
        }
    }

}
package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.artux.pda.app.DataManager
import net.artux.pda.generated.models.RegisterUserDto
import net.artux.pda.generated.models.Status
import net.artux.pda.models.user.LoginUser
import net.artux.pda.models.user.UserMapper
import net.artux.pda.models.user.UserModel
import net.artux.pda.repositories.UserRepository
import net.artux.pda.repositories.util.Result
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private var userRepository: UserRepository,
    var userMapper: UserMapper,
    var dataManager: DataManager
) : ViewModel() {

    var member: MutableLiveData<Result<UserModel>> =
        MutableLiveData(userRepository.getCachedMember().map { userMapper.dto(it) })

    var status: MutableLiveData<Result<Status>> = MutableLiveData()

    fun isLoggedIn(): Boolean {
        return member.value!!.isSuccess()
    }

    fun login(loginUser: LoginUser) {
        dataManager.setLoginUser(loginUser)
        viewModelScope.launch {
            member.postValue(userRepository.getMember().map { userMapper.dto(it) })
        }
    }

    fun registerUser(registerUserDto: RegisterUserDto) {
        viewModelScope.launch {
            status.postValue(userRepository.registerUser(registerUserDto))
        }
    }
}
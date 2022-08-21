package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.artux.pda.app.DataManager
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.model.user.LoginUser
import net.artux.pda.model.user.RegisterUserModel
import net.artux.pda.model.user.UserModel
import net.artux.pda.repositories.UserRepository
import net.artux.pda.repositories.util.Result
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private var userRepository: UserRepository,
    var userMapper: UserMapper,
    var statusMapper: StatusMapper,
    var dataManager: DataManager
) : ViewModel() {

    var member: MutableLiveData<Result<UserModel>> =
        MutableLiveData(userRepository.getCachedMember().map { userMapper.dto(it) })

    var status: MutableLiveData<Result<StatusModel>> = MutableLiveData()

    fun isLoggedIn(): Boolean {
        return member.value!!.isSuccess()
    }

    fun login(loginUser: LoginUser) {
        dataManager.setLoginUser(loginUser)
        viewModelScope.launch {
            member.postValue(userRepository.getMember().map { userMapper.dto(it) })
        }
    }

    fun registerUser(registerUserDto: RegisterUserModel) {
        viewModelScope.launch {
            status.postValue(userRepository.registerUser(userMapper.dto(registerUserDto)).map { statusMapper.model(it) })
        }
    }
}
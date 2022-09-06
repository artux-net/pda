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
import net.artux.pda.model.user.LoginUser
import net.artux.pda.model.user.RegisterUserModel
import net.artux.pda.model.user.UserModel
import net.artux.pda.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private var userRepository: UserRepository,
    var userMapper: UserMapper,
    var statusMapper: StatusMapper,
    var dataManager: DataManager
) : ViewModel() {

    var member: MutableLiveData<UserModel> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()

    fun isLoggedIn(): Boolean {
        return dataManager.isAuthenticated
    }

    fun login() {
        viewModelScope.launch {
            userRepository.getMember()
                .onSuccess { member.postValue(userMapper.model(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun login(loginUser: LoginUser) {
        dataManager.setLoginUser(loginUser)
        login()
    }

    fun registerUser(registerUserDto: RegisterUserModel) {
        viewModelScope.launch {
            userRepository
                .registerUser(userMapper.model(registerUserDto))
                .onSuccess {
                    status.postValue(statusMapper.model(it))
                }.onFailure {
                    status.postValue(StatusModel(it))
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            userRepository.resetPassword(email)
                .onSuccess {
                    status.postValue(statusMapper.model(it))
                }.onFailure {
                    status.postValue(StatusModel(it))
                }
        }
    }
}
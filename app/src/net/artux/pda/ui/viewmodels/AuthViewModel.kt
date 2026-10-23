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
import net.artux.pdanetwork.ApiClient
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private var userRepository: UserRepository,
    var userMapper: UserMapper,
    var statusMapper: StatusMapper,
    var dataManager: DataManager,
    val apiClient: ApiClient
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
        apiClient.setCredentials(loginUser.emailOrLogin, loginUser.password)
        dataManager.setLoginUser(loginUser) // todo remove
        login()
    }

    /**
     * Exchanges the server auth code obtained from Google Play Games sign-in
     * for a pdanetwork session. The backend creates an account on the fly
     * for a player it has never seen before.
     */
    fun loginWithGooglePlayGames(serverAuthCode: String) {
        viewModelScope.launch {
            userRepository.authenticateWithGooglePlayGames(serverAuthCode)
                .onSuccess {
                    dataManager.setJwtToken(it.token)
                    login()
                }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun registerUser(registerUserDto: RegisterUserModel) {
        viewModelScope.launch {
            apiClient.apiAuthorizations.clear()
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

    fun logout() {
        dataManager.removeAllData()
        userRepository.clearMemberCache()
    }
}
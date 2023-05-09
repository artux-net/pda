package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.app.DataManager
import net.artux.pda.common.PropertyFields
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.model.user.UserModel
import net.artux.pda.repositories.UserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private var userRepository: UserRepository,
    var dataManager: DataManager,
    var storyMapper: StoryMapper,
    var userMapper: UserMapper,
    var statusMapper: StatusMapper,
    var firebaseRemoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    var member: MutableLiveData<UserModel> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var storyData: MutableLiveData<StoryDataModel> = MutableLiveData()

    fun updateFromCache() {
        userRepository.getCachedMember()
            .map { userMapper.model(it) }
            .onSuccess { member.postValue(it) }
        userRepository.getCachedData()
            .map { storyMapper.dataModel(it) }
            .onSuccess { storyData.postValue(it) }
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

    fun getId(): UUID {
        return userRepository.getCachedMember().map { it.id }.getOrDefault(UUID.randomUUID())
    }

    fun signOut() {
        dataManager.removeAllData()
        userRepository.clearMemberCache()
    }

    fun isChatAllowed(): Boolean {
        val userModel: UserModel? = getFromCache()
        val atLeastOneOver = storyData.value?.storyStates?.any { it.over } ?: false
        val xpChatLimit = firebaseRemoteConfig.getLong(PropertyFields.XP_CHAT_LIMIT)
        return (userModel != null && xpChatLimit <= userModel.xp) || atLeastOneOver
    }

}
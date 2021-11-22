package net.artux.pda.ui.fragments.profile

import androidx.lifecycle.*
import net.artux.pdalib.Profile
import javax.inject.Inject

class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    var userRepository: UserRepository
) : ViewModel() {
    val userId : Int = savedStateHandle["uid"] ?:
    throw IllegalArgumentException("missing user id")
    var user : MutableLiveData<Profile> = userRepository.getProfile(userId)


    fun load(pdaId : Int){
        userRepository.getProfile(pdaId, user)
    }

}
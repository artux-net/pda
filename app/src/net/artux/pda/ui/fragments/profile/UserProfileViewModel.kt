package net.artux.pda.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import net.artux.pdalib.Profile
import javax.inject.Inject

class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    var userRepository: UserRepository
) : ViewModel() {
    val userId : Int = savedStateHandle["uid"] ?:
    throw IllegalArgumentException("missing user id")
    var user : LiveData<Profile> = userRepository.getProfile(userId)


    fun load(pdaId : Int){
        user = userRepository.getProfile(pdaId)
    }

}
package net.artux.pda.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.artux.pda.repositories.Result
import net.artux.pda.repositories.UserRepository
import net.artux.pdalib.Member
import net.artux.pdalib.Profile
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    var userRepository: UserRepository
) : ViewModel() {
    var profile: MutableLiveData<Profile> = MutableLiveData(getCachedProfile(getId()))
    var member: MutableLiveData<Member> = MutableLiveData(null)

    private fun getCachedProfile(pdaId: Int) : Profile? {
        val response = userRepository.getCachedProfile(pdaId)
        return if (response is Result.Success)
            response.data
        else null
    }

    fun updateProfile(pdaId: Int) {
        GlobalScope.launch {
            val response = userRepository.getProfile(pdaId)
            if (response is Result.Success)
                profile.postValue(response.data)
        }
    }

    fun getId():Int{
        return (userRepository.getCachedMember() as Result.Success).data.pdaId
    }

}
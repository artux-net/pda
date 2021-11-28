package net.artux.pda.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.artux.pda.repositories.Result
import net.artux.pda.repositories.UserRepository
import net.artux.pdalib.Member
import javax.inject.Inject

class MemberViewModel @Inject constructor(
    var userRepository: UserRepository) : ViewModel() {
    var member: MutableLiveData<Result<Member>> = MutableLiveData(userRepository.getCachedMember())

    fun updateMember() {
        GlobalScope.launch {
            member.postValue(userRepository.getMember())
        }
    }

    fun syncMember(map: HashMap<String, List<String>>) {
        GlobalScope.launch {
            member.postValue(userRepository.syncMember(map))
        }
    }

    fun updateMemberWithReset() {
        GlobalScope.launch {
            userRepository.clearMemberCache()
            member.postValue(userRepository.getMember())
        }
    }

    fun resetData() {
        GlobalScope.launch {
            member.postValue(userRepository.resetData())
        }
    }

    fun getId():Int{
        return (userRepository.getCachedMember() as Result.Success).data.pdaId
    }

}
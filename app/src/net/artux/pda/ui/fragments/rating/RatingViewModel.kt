package net.artux.pda.ui.fragments.rating

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.artux.pda.models.user.UserMapper
import net.artux.pda.repositories.UserRepository
import net.artux.pda.repositories.util.Result
import java.util.*
import javax.inject.Inject

class RatingViewModel @Inject constructor(
    var userRepository: UserRepository, var userMapper: UserMapper
) : ViewModel() {

    var list: MutableLiveData<Result<List<UserInfo>>> =
        MutableLiveData(Result.Success(Collections.emptyList()))
    var page: Int = 1

    fun updateList() {
        viewModelScope.launch {
            val response = userRepository.getRatingPage(page)
            list.postValue(response.map { userMapper.ratingModels(it) })
        }
    }

    fun previousPage() {
        if (page > 2)
            page--;
        updateList()
    }

    fun nextPage() {
        page++;
        updateList()
    }

}
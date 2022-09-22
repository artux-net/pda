package net.artux.pda.ui.fragments.rating

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    var userRepository: UserRepository, var userMapper: UserMapper
) : ViewModel() {

    var list: MutableLiveData<List<UserInfo>> = MutableLiveData()
    var page: Int = 1

    fun updateList() {
        viewModelScope.launch {
            val response = userRepository.getRatingPage(page)
            response.onSuccess {
                list.postValue(userMapper.ratingModels(it))
            }

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
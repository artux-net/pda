package net.artux.pda.ui.fragments.rating

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.mapper.UserMapper
import net.artux.pda.repositories.UserRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    var userRepository: UserRepository,
    var userMapper: UserMapper
) : ViewModel() {

    var list: MutableLiveData<MutableList<UserInfo>> = MutableLiveData(LinkedList())
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var state: MutableLiveData<Boolean> = MutableLiveData()
    var page: Int = 1

    fun start() {
        page = 1
        state.postValue(true)
        viewModelScope.launch {
            userRepository.getRatingPage(1)
                .map {
                    userMapper.ratingModels(it)
                }
                .onSuccess {
                    list.postValue(it)
                }
                .onFailure {
                    status.postValue(StatusModel(it))
                }
            state.postValue(false)
        }

    }

    private fun updateList() {
        state.postValue(true)
        viewModelScope.launch {
            userRepository.getRatingPage(page)
                .map {
                    userMapper.ratingModels(it)
                }
                .onSuccess {
                    if (it.size > 0) {
                        list.postValue(it)
                    }else{
                        status.postValue(StatusModel("There are no more users."))
                    }
                }
                .onFailure {
                    status.postValue(StatusModel(it))
                }
            state.postValue(false)
        }
    }

    fun previousPage() {
        if (page > 2)
            page--
        updateList()
    }

    fun nextPage() {
        page++
        updateList()
    }

}
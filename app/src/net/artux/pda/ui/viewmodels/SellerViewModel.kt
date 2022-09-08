package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.items.SellerModel
import net.artux.pda.model.mapper.SellerMapper
import net.artux.pda.repositories.SellerRepository
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    var repository: SellerRepository
) : ViewModel() {
    var seller: MutableLiveData<SellerModel> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var sellerMapper: SellerMapper = SellerMapper.INSTANCE

    fun update(id: Long) {
        viewModelScope.launch {
            repository.getSeller(id)
                .onSuccess { seller.postValue(sellerMapper.model(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun updateFromCache(id: Long) {
        seller.postValue(
            repository.getCachedSeller(id)
            .map { sellerMapper.model(it) }
            .getOrNull())
    }

}
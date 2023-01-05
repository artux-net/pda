package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.artux.pda.model.StatusModel
import net.artux.pda.model.items.SellerModel
import net.artux.pda.model.mapper.SellerMapper
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.repositories.SellerRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    var repository: SellerRepository
) : ViewModel() {
    var seller: MutableLiveData<SellerModel> = MutableLiveData()
    var status: MutableLiveData<StatusModel> = MutableLiveData()
    var statusMapper: StatusMapper = StatusMapper.INSTANCE
    var sellerMapper: SellerMapper = SellerMapper.INSTANCE

    fun update(id: Long) {
        viewModelScope.launch {
            repository.getSeller(id)
                .onSuccess { seller.postValue(sellerMapper.model(it)) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    private suspend fun actionWithItem(
        type: SellerRepository.OperationType,
        uuid: UUID,
        quantity: Int
    ): Result<StatusModel> {
        val sellerId = seller.value!!.id
        return repository.actionWithItem(type, uuid, sellerId, quantity)
            .map { statusMapper.model(it) }
    }

    fun buyItem(uuid: UUID, quantity: Int) {
        viewModelScope.launch {
            actionWithItem(SellerRepository.OperationType.BUY, uuid, quantity)
                .onSuccess { status.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun sellItem(uuid: UUID, quantity: Int) {
        viewModelScope.launch {
            actionWithItem(SellerRepository.OperationType.SELL, uuid, quantity)
                .onSuccess { status.postValue(it) }
                .onFailure { status.postValue(StatusModel(it)) }
        }
    }

    fun getSellerCoefficient(): Float {
        return if (seller.value != null)
            seller.value!!.sellCoefficient
        else 0.8f
    }

    fun getBuyCoefficient(): Float {
        return if (seller.value != null)
            seller.value!!.buyCoefficient
        else 1.2f
    }
}
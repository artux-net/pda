package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.artux.pda.model.Summary
import net.artux.pda.repositories.SummaryRepository
import javax.inject.Inject

class SummaryViewModel @Inject constructor(
    var repository: SummaryRepository
) : ViewModel() {
    var summary: MutableLiveData<Summary> = MutableLiveData()

    fun getCachedSummary(id: String) : MutableLiveData<Summary> {
        /*val response = repository.getCachedSummary(id)
        if (response is Result.Success)
            summary.postValue(response.data!!)
        return summary TODO*/
        return summary
    }

    fun removeSummary(id: String) {
        repository.remove(id)
    }

    fun getAllIds() : Array<String> {
        return repository.getAllDates()
    }

    fun putSummary(id: String, summary: Summary){
        repository.putSummary(id, summary)
    }

}
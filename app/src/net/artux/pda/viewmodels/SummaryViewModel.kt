package net.artux.pda.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.artux.pda.repositories.Result
import net.artux.pda.repositories.SummaryRepository
import net.artux.pdalib.Summary
import javax.inject.Inject

class SummaryViewModel @Inject constructor(
    var repository: SummaryRepository
) : ViewModel() {
    var summary: MutableLiveData<Summary> = MutableLiveData()

    fun getCachedSummary(id: String) : MutableLiveData<Summary> {
        val response = repository.getCachedSummary(id)
        if (response is Result.Success)
            summary.value = response.data
        return summary
    }

    fun getAllIds() : Array<String> {
        return repository.getAllDates()
    }

    fun putSummary(id: String, summary: Summary){
        repository.putSummary(id, summary)
    }

}
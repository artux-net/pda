package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.artux.pda.model.Summary
import net.artux.pda.repositories.SummaryRepository
import net.artux.pda.repositories.util.Result
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    var repository: SummaryRepository
) : ViewModel() {
    var summary: MutableLiveData<Summary> = MutableLiveData()
    var summaries: MutableLiveData<List<Summary>> = MutableLiveData()

    fun getCachedSummary(id: String): MutableLiveData<Summary> {
        val response = repository.getCachedSummary(id)
        if (response is Result.Success)
            summary.postValue(response.data)
        return summary
    }

    fun openSummary(id: String){
        summary.postValue(repository
            .getCachedSummary(id)
            .getOrNull())
    }

    fun removeSummary(id: String) {
        repository.remove(id)
        updateSummaries()
        summary.postValue(null)
    }

    fun putSummary(id: String, summary: Summary) {
        repository.putSummary(id, summary)
    }

    fun updateSummaries(){
        summaries.postValue(repository.getAll())
    }

}
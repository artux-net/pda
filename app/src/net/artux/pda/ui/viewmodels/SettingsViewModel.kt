package net.artux.pda.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.artux.pda.repositories.LogRepository

@HiltViewModel
class SettingsViewModel @javax.inject.Inject constructor(
    var logRepository: LogRepository
) : ViewModel() {

    val log: MutableLiveData<String> = MutableLiveData()


    fun update() {
        log.postValue(getLogInString())
    }

    fun resetLogFile() {
        logRepository.resetFile()
    }

    fun getLogInString(): String {
        val logBuilder = StringBuilder()
        for (log in logRepository.getLogs()) {
            logBuilder
                .append(log)
                .append('\n')
        }
        return logBuilder.toString()
    }

}
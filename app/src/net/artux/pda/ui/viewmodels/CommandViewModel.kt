package net.artux.pda.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.datatransport.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import net.artux.pda.model.mapper.StatusMapper
import net.artux.pda.repositories.CommandController
import net.artux.pda.repositories.MissionController
import net.artux.pda.ui.viewmodels.event.ScreenDestination
import net.artux.pda.ui.viewmodels.util.SingleLiveEvent
import net.artux.pda.utils.AdType

@HiltViewModel
class CommandViewModel @javax.inject.Inject constructor(
    var missionController: MissionController,
    var commandController: CommandController,
    var statusMapper: StatusMapper
) : ViewModel() {


    val sellerEvent: SingleLiveEvent<Event<Int>> get() = commandController.sellerEvent
    val exitEvent: SingleLiveEvent<ScreenDestination> get() = commandController.exitEvent
    val adEvent: SingleLiveEvent<AdType> get() = commandController.adEvent

    fun process(actions: Map<String, MutableList<String>>) {
        commandController.process(actions)
    }

    fun processWithServer(actions: Map<String, MutableList<String>>) {
        commandController.processWithServer(actions)
    }

    fun cacheCommands(actions: Map<String, MutableList<String>>) {
        commandController.cacheCommands(actions)
    }

    fun clearCommands() {
        commandController.clearCache()
    }
}
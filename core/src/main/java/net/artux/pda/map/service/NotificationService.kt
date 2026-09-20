package net.artux.pda.map.service

import com.badlogic.gdx.Gdx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.map.controller.notification.NotificationController
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.engine.utils.LocaleBundle
import javax.inject.Inject

@PerGameMap
class NotificationService @Inject constructor(
    val dataRepository: DataRepository,
    val notificationController: NotificationController,
    val localeBundle: LocaleBundle
){

    init {
        CoroutineScope(Dispatchers.Default).launch {
            dataRepository.storyDataModelFlow.collect {
                Gdx.app.postRunnable { update(it) }
            }
        }
    }

    private fun update(it: StoryDataModel) {
        notificationController.addMessage(localeBundle.get("sync.success"))
    }


}
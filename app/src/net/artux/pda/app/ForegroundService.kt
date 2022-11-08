package net.artux.pda.app

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.artux.pda.gdx.MapEngine
import net.artux.pda.model.mapper.StoryMapper
import net.artux.pda.repositories.QuestRepository
import net.artux.pdanetwork.model.CommandBlock
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
open class ForegroundService : Service() {
    private lateinit var binder: ServiceBinder

    @Inject
    lateinit var questRepository: QuestRepository

    @Inject
    lateinit var storyMapper: StoryMapper


    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        binder = ServiceBinder()
        Timber.d("ForegroundService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ForegroundService onDestroy")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("ForegroundService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(arg0: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    fun applyActions(actions: Map<String, List<String>>) {
        scope.launch {
            questRepository.syncMember(CommandBlock().actions(actions))
                .map { storyMapper.dataModel(it) }
                .onSuccess {
                    val intent = Intent(MapEngine.RECEIVER_INTENT)
                    intent.putExtra(MapEngine.RECEIVE_STORY_DATA, it)
                    LocalBroadcastManager.getInstance(this@ForegroundService).sendBroadcast(intent)
                }
                .onFailure {
                    val intent = Intent(MapEngine.RECEIVER_INTENT)
                    intent.putExtra(MapEngine.RECEIVE_ERROR, it)
                    LocalBroadcastManager.getInstance(this@ForegroundService).sendBroadcast(intent)
                }
        }
    }

    inner class ServiceBinder : Binder() {
        val service: ForegroundService
            get() = this@ForegroundService
    }
}
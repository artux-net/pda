package net.artux.pda.repositories

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.scripting.IQuestSoundManager
import net.artux.pda.utils.URLHelper
import timber.log.Timber
import javax.inject.Singleton


@Singleton
class QuestSoundManager(
    override val mediaPlayer: MediaPlayer,
    override val soundPool: SoundPool,
    val context: Context
) :
    IQuestSoundManager {

    private val mediaScope = CoroutineScope(Dispatchers.Main)
    var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
    var maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
    var leftVolume = curVolume / maxVolume
    var rightVolume = curVolume / maxVolume
    override var muted = false

    init {
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, _ ->
            if (!muted)
                soundPool.play(sampleId, leftVolume, rightVolume, 1, 0, 1f)
        }
        mediaPlayer.setOnPreparedListener(this)
    }


    val loadedSounds: MutableMap<String, Int> = linkedMapOf()

    override fun playSound(path: String) {
        val id = if (loadedSounds.containsKey(path))
            loadedSounds[path]
        else {
            val fileDescriptor = context.assets.openFd(path)
            loadedSounds[path] = soundPool.load(fileDescriptor, 1)
            loadedSounds[path]
        }
        Timber.i("Sound $path loaded as $id")
    }

    override fun pauseSound(path: String) {
        val id = loadedSounds[path]
        if (id != null) {
            soundPool.pause(id)
        }
    }

    override fun playMusic(path: String, loop: Boolean) = mediaScope.launch {
        mediaPlayer.stop()
        mediaPlayer.isLooping = loop
        try {
            val descriptor = context.assets.openFd(path)
            mediaPlayer.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )
            Timber.i("Music $path played from local file")
            mediaPlayer.prepare()
        } catch (_: Exception) {
            loadOnlineResource(path)
        }

    }

    override fun loadOnlineResource(path: String) {
        try {
            Timber.i("Try to load $path from net")
            mediaPlayer.setDataSource(URLHelper.getResourceURL(path))
            mediaPlayer.prepareAsync()
        } catch (_: Exception) {

        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        mediaPlayer.start()
    }

    override var wasPlaying = false

    override fun stop() {
        pause()
        mediaPlayer.stop()
    }

    override fun pause() {
        Timber.i("Pause audio")
        soundPool.autoPause()
        wasPlaying = mediaPlayer.isPlaying
        if (wasPlaying)
            mediaPlayer.pause()
    }

    override fun resume() {
        Timber.i("Resume audio")
        soundPool.autoResume()
        if (wasPlaying)
            mediaPlayer.start()
    }


    override fun mute() {
        if (!muted) {
            mediaPlayer.setVolume(0f, 0f)
            pause()
        } else {
            mediaPlayer.setVolume(1f, 1f)
            resume()
        }
        muted = !muted
        Timber.i("Muted = $muted")
    }

}
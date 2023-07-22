package net.artux.pda.repositories

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.utils.URLHelper
import timber.log.Timber
import javax.inject.Singleton


@Singleton
class QuestSoundManager(
    val mediaPlayer: MediaPlayer,
    val soundPool: SoundPool,
    val context: Context
) :
    OnPreparedListener {

    private val mediaScope = CoroutineScope(Dispatchers.Main)
    var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
    var maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
    var leftVolume = curVolume / maxVolume
    var rightVolume = curVolume / maxVolume
    var muted = false

    init {
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, _ ->
            if (!muted)
                soundPool.play(sampleId, leftVolume, rightVolume, 1, 0, 1f)
        }
        mediaPlayer.setOnPreparedListener(this)
    }


    val loadedSounds: MutableMap<String, Int> = linkedMapOf()

    fun playSound(path: String) {
        val id = if (loadedSounds.containsKey(path))
            loadedSounds[path]
        else {
            val fileDescriptor = context.assets.openFd(path)
            loadedSounds[path] = soundPool.load(fileDescriptor, 1)
            loadedSounds[path]
        }
        Timber.i("Sound $path loaded as $id")
    }

    fun pauseSound(path: String) {
        val id = loadedSounds[path]
        if (id != null) {
            soundPool.pause(id)
        }
    }

    fun playMusic(path: String, b: Boolean) = mediaScope.launch {
        mediaPlayer.stop()
        mediaPlayer.isLooping = b
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

    private fun loadOnlineResource(path: String) {
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

    var wasPlaying = false

    fun stop() {
        pause()
        mediaPlayer.stop()
    }

    fun pause() {
        Timber.i("Pause audio")
        soundPool.autoPause()
        wasPlaying = mediaPlayer.isPlaying
        if (wasPlaying)
            mediaPlayer.pause()
    }

    fun resume() {
        Timber.i("Resume audio")
        soundPool.autoResume()
        if (wasPlaying)
            mediaPlayer.start()
    }


    fun mute() {
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
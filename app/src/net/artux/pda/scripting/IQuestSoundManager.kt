package net.artux.pda.scripting

import android.media.MediaPlayer
import android.media.SoundPool
import kotlinx.coroutines.Job

interface IQuestSoundManager : MediaPlayer.OnPreparedListener {
    val mediaPlayer: MediaPlayer
    val soundPool: SoundPool
    var muted: Boolean
    var wasPlaying: Boolean
    fun playSound(path: String)
    fun pauseSound(path: String)
    fun playMusic(path: String, loop: Boolean): Job
    fun loadOnlineResource(path: String)

    override fun onPrepared(mp: MediaPlayer)
    fun stop()
    fun pause()
    fun resume()
    fun mute()
}
package net.artux.pda.di;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import net.artux.pda.repositories.CommandController;
import net.artux.pda.repositories.QuestSoundManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;


@Module
@InstallIn(SingletonComponent.class)
public class ComponentModule {

    @Provides
    @Singleton
    public QuestSoundManager soundManager(MediaPlayer mediaPlayer, SoundPool pool, @ApplicationContext Context context) {
        return new QuestSoundManager(mediaPlayer, pool, context);
    }

    @Provides
    public MediaPlayer mediaPlayer(AudioAttributes attributes) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(attributes);
        return mediaPlayer;
    }

    @Provides
    public SoundPool soundPool(AudioAttributes attributes) {
        return new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build();
    }

    @Provides
    public AudioAttributes attributes() {
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build();
    }

}

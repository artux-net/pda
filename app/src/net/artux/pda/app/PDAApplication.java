package net.artux.pda.app;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.yandex.mobile.ads.common.MobileAds;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.common.PropertyFields;
import net.artux.pda.utils.URLHelper;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class PDAApplication extends Application {

    @Inject
    protected List<Timber.Tree> logForest;
    @Inject
    protected Properties properties;
    @Inject
    protected SoundPool soundPool;
    @Inject
    protected MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        super.onCreate();
        for (Timber.Tree tree : logForest) {
            Timber.plant(tree);
            Timber.i("%s - логирование включено", tree.getClass().getSimpleName());
        }
        Timber.i(getString(R.string.hello_message));
        Timber.i("Сталкерский ПДА запущен, версия: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + "), тип: " + BuildConfig.BUILD_TYPE);
        Timber.i("Режим тестирования: %s", properties.get(PropertyFields.TESTER_MODE));
        MobileAds.initialize(this, () -> Timber.i("Инициализирован Yandex ADS SDK"));
        URLHelper.init(properties);
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        soundPool.release();
        mediaPlayer.release();
    }

    public long getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }
}

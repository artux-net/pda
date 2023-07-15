package net.artux.pda.app;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.google.firebase.FirebaseApp;
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

    public static int[] group_avatars = {
            R.drawable.g0,
            R.drawable.g1,
            R.drawable.g2,
            R.drawable.g3,
            R.drawable.g4,
            R.drawable.g5,
            R.drawable.g6,
            R.drawable.g7,
            R.drawable.g8
    };

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        super.onCreate();
        for (Timber.Tree tree : logForest) {
            Timber.plant(tree);
            Timber.i("%s - логирование включено", tree.getClass().getSimpleName());
        }
        Timber.i(getString(R.string.hello_message));
        Timber.i("Сталкерский ПДА запущен, версия: " + BuildConfig.VERSION_NAME  + " (" + BuildConfig.VERSION_CODE + "), тип: " + BuildConfig.BUILD_TYPE);
        Timber.i("Режим тестирования: %s", isTesterMode());
        MobileAds.initialize(this, () -> Timber.i("Инициализирован Yandex ADS SDK"));
        URLHelper.init(properties);
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isTesterMode() {
        return properties.get(PropertyFields.TESTER_MODE).equals(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        soundPool.release();
        mediaPlayer.release();
    }
}

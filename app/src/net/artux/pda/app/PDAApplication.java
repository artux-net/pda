package net.artux.pda.app;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;

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
    protected List<Timber.Tree> forest;
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
        //Appodeal.setTesting(BuildConfig.DEBUG);
        for (Timber.Tree tree : forest) {
            Timber.plant(tree);
            Timber.i("%s planted", tree.getClass().getSimpleName());
        }
        Timber.i("App started.");
        MobileAds.initialize(this, () -> Timber.i("Mobile ADS SDK initialized"));
        URLHelper.init(properties);
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isTesterMode() {
        return properties.getProperty(PropertyFields.TESTER_MODE, "false")
                .equals("true");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        soundPool.release();
        mediaPlayer.release();
    }
}

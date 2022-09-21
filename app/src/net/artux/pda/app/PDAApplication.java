package net.artux.pda.app;

import android.app.Application;

import net.artux.pda.R;
import net.artux.pda.api.PdaAPI;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class PDAApplication extends Application {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Timber.Tree tree;
    @Inject
    protected PdaAPI pdaAPI;

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
        super.onCreate();
        Timber.i("App started.");
        Timber.plant(tree);
        Timber.i("%s planted", tree.getClass().getSimpleName());
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PdaAPI getOldApi() {
        return pdaAPI;
    }
}

package net.artux.pda.app;

import android.app.Application;

import net.artux.pda.R;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class PDAApplication extends Application {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected List<Timber.Tree> forest;

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
        for (Timber.Tree tree : forest) {
            Timber.plant(tree);
            Timber.i("%s planted", tree.getClass().getSimpleName());
        }
        Timber.i("App started.");
    }

    public DataManager getDataManager() {
        return dataManager;
    }

}

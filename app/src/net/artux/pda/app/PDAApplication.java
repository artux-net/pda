package net.artux.pda.app;

import android.app.Application;

import net.artux.pda.R;
import net.artux.pda.repositories.QuestRepository;
import net.artux.pda.repositories.SummaryRepository;
import net.artux.pda.repositories.UserRepository;
import net.artux.pda.services.PdaAPI;
import net.artux.pdanetwork.api.DefaultApi;

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
    protected UserRepository userRepository;
    @Inject
    protected SummaryRepository summaryRepository;
    @Inject
    protected QuestRepository questRepository;
    @Inject
    protected DefaultApi defaultApi;
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
        /*MobileAds.initialize(this, initializationStatus -> {
            Timber.d("Ads initialization");
            initializationStatus.getAdapterStatusMap().forEach((s, adapterStatus) ->
                    Timber.d(s + " : " + adapterStatus.getDescription() + " latency: " + adapterStatus.getLatency()));
        });*/

    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public QuestRepository getQuestRepository() {
        return questRepository;
    }

    public SummaryRepository getSummaryRepository() {
        return summaryRepository;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public DefaultApi getDefaultApi() {
        return defaultApi;
    }

    public PdaAPI getOldApi() {
        return pdaAPI;
    }
}
